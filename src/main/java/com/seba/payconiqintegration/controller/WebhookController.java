package com.seba.payconiqintegration.controller;

import com.seba.payconiqintegration.model.PayconiqWebhookPayload;
import com.seba.payconiqintegration.service.IdempotencyService;
import com.seba.payconiqintegration.service.PaymentProcessingService;
import com.seba.payconiqintegration.service.WebhookLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("${payconiq.webhook.secret}")
    private String webhookSecret;

    private final PaymentProcessingService paymentProcessingService;
    private final IdempotencyService idempotencyService;
    private final WebhookLogService webhookLogService;

    @Autowired
    public WebhookController(
            PaymentProcessingService paymentProcessingService,
            IdempotencyService idempotencyService,
            WebhookLogService webhookLogService) {
        this.paymentProcessingService = paymentProcessingService;
        this.idempotencyService = idempotencyService;
        this.webhookLogService = webhookLogService;
    }

    /**
     * Endpoint pour recevoir les notifications de Payconiq
     */
    @PostMapping("/payconiq")
    public ResponseEntity<String> handlePayconiqWebhook(
            @RequestBody PayconiqWebhookPayload payload,
            @RequestHeader("X-Signature") Optional<String> signatureHeader,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        // Générer un ID de requête si pas fourni
        String finalRequestId = (requestId == null || requestId.isEmpty())
                ? "req-" + java.util.UUID.randomUUID().toString()
                : requestId;

        // Journaliser le webhook reçu
        webhookLogService.logWebhook(payload, finalRequestId);

        // Vérification de la signature si disponible
        if (signatureHeader.isPresent() && !verifySignature(payload, signatureHeader.get())) {
            logger.warn("Signature du webhook invalide");
            return ResponseEntity.badRequest().body("Signature invalide");
        }

        try {
            // Créer un identifiant unique pour ce webhook (paymentId + statut)
            String webhookEventId = payload.getPaymentId() + "_" + payload.getStatus();

            // Vérifier l'idempotence ne traiter que si c'est un nouvel événement
            if (idempotencyService.isNewEvent(webhookEventId)) {
                // Traiter la notification selon son statut
                switch (payload.getStatus()) {
                    case "PENDING":
                        logger.info("Paiement en attente: {}", payload.getPaymentId());
                        paymentProcessingService.handlePendingPayment(payload);
                        break;
                    case "SUCCEEDED":
                        logger.info("Paiement réussi: {}", payload.getPaymentId());
                        paymentProcessingService.handleSuccessfulPayment(payload);
                        break;
                    case "FAILED":
                        logger.info("Paiement échoué: {}", payload.getPaymentId());
                        paymentProcessingService.handleFailedPayment(payload);
                        break;
                    case "CANCELLED":
                        logger.info("Paiement annulé: {}", payload.getPaymentId());
                        paymentProcessingService.handleCancelledPayment(payload);
                        break;
                    default:
                        logger.warn("Statut de paiement inconnu: {}", payload.getStatus());
                        break;
                }
            } else {
                logger.info("Webhook déjà traité, ignoré: {}", webhookEventId);
            }

            // Répondre avec succès
            return ResponseEntity.ok("Webhook traité avec succès");

        } catch (Exception e) {
            logger.error("Erreur lors du traitement du webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erreur lors du traitement");
        }
    }

    /**
     * Vérifie la signature du webhook avec le secret partagé
     */
    private boolean verifySignature(PayconiqWebhookPayload payload, String receivedSignature) {
        try {
            // Construire la chaîne à vérifier (normalement, elle contiendrait la charge utile complète)
            String dataToSign = payload.getPaymentId() + payload.getStatus() + payload.getAmount();

            // Algorithme de signature (HMAC-SHA256 couramment utilisé)
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(secretKeySpec);

            // Calculer la signature
            byte[] hmacBytes = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(hmacBytes);

            // Comparer les signatures
            return calculatedSignature.equals(receivedSignature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Erreur lors de la vérification de la signature: {}", e.getMessage(), e);
            return false;
        }
    }
}