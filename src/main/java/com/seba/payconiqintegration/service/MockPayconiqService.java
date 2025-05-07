package com.seba.payconiqintegration.service;

import com.seba.payconiqintegration.model.PayconiqPaymentRequest;
import com.seba.payconiqintegration.model.PayconiqPaymentResponse;
import com.seba.payconiqintegration.service.inter.IPayconiqService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Profile("test") // Ce service sera utilisé uniquement avec le profil "test"
public class MockPayconiqService implements IPayconiqService {

    // Stockage en mémoire des paiements simulés
    private final Map<String, PayconiqPaymentResponse> payments = new HashMap<>();

    @Override
    public Mono<PayconiqPaymentResponse> createPayment(PayconiqPaymentRequest paymentRequest) {
        String paymentId = UUID.randomUUID().toString();

        // Création d'une réponse simulée
        PayconiqPaymentResponse response = new PayconiqPaymentResponse();
        response.setPaymentId(paymentId);
        response.setAmount(paymentRequest.getAmount());
        response.setDescription(paymentRequest.getDescription());
        response.setStatus("PENDING"); // État initial du paiement
        response.setCreatedAt(LocalDateTime.now().toString());
        response.setDeepLinkQrCode("https://mock-payconiq-qr-code.com/" + paymentId);
        response.setQrCode("data:image/png;base64,mockQrCodeData");

        // Stockage du paiement
        payments.put(paymentId, response);

        return Mono.just(response);
    }

    @Override
    public Mono<PayconiqPaymentResponse> getPaymentDetails(String paymentId) {
        PayconiqPaymentResponse response = payments.get(paymentId);
        if (response != null) {
            return Mono.just(response);
        } else {
            return Mono.error(new RuntimeException("Payment not found with ID: " + paymentId));
        }
    }

    @Override
    public Mono<Void> cancelPayment(String paymentId) {
        if (payments.containsKey(paymentId)) {
            PayconiqPaymentResponse payment = payments.get(paymentId);
            payment.setStatus("CANCELLED");
            return Mono.empty();
        } else {
            return Mono.error(new RuntimeException("Cannot cancel payment. Payment not found with ID: " + paymentId));
        }
    }

    // Méthode utilitaire pour simuler un paiement réussi
    public void simulateSuccessfulPayment(String paymentId) {
        if (payments.containsKey(paymentId)) {
            PayconiqPaymentResponse payment = payments.get(paymentId);
            payment.setStatus("SUCCEEDED");
            payment.setCompletedAt(LocalDateTime.now().toString());
        }
    }

    // Méthode utilitaire pour simuler un paiement échoué
    public void simulateFailedPayment(String paymentId) {
        if (payments.containsKey(paymentId)) {
            PayconiqPaymentResponse payment = payments.get(paymentId);
            payment.setStatus("FAILED");
            payment.setCompletedAt(LocalDateTime.now().toString());
        }
    }
}
