package com.seba.payconiqintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seba.payconiqintegration.model.PayconiqWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsable de la journalisation des webhooks reçus
 */
@Service
public class WebhookLogService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookLogService.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookLogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Journalise un webhook reçu
     * Dans une application réelle, on pourrait stocker ces informations dans une base de données
     */
    public void logWebhook(PayconiqWebhookPayload payload, String requestId) {
        try {
            // Convertir la charge utile en JSON pour l'enregistrement
            String payloadJson = objectMapper.writeValueAsString(payload);

            // Log détaillé
            logger.info("Webhook reçu [{}]: paymentId={}, status={}, time={}, payload={}",
                    requestId, payload.getPaymentId(), payload.getStatus(), Instant.now(), payloadJson);

            // En production, vous pourriez stocker ces informations dans une base de données
            // webhookRepository.save(new WebhookLog(requestId, payload.getPaymentId(), payload.getStatus(), payloadJson, Instant.now()));

        } catch (Exception e) {
            logger.error("Erreur lors de la journalisation du webhook: {}", e.getMessage(), e);
        }
    }
}