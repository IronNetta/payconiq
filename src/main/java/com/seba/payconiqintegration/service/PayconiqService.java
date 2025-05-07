package com.seba.payconiqintegration.service;

import com.seba.payconiqintegration.model.PayconiqPaymentRequest;
import com.seba.payconiqintegration.model.PayconiqPaymentResponse;
import com.seba.payconiqintegration.service.inter.IPayconiqService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Profile("!test") // Ce service sera utilisé quand le profil "test" n'est PAS actif
public class PayconiqService implements IPayconiqService {

    private final WebClient webClient;

    @Value("${payconiq.api.key}")
    private String apiKey;

    @Value("${payconiq.merchant.id}")
    private String merchantId;

    @Value("${payconiq.callback.url}")
    private String callbackUrl;

    public PayconiqService(@Value("${payconiq.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<PayconiqPaymentResponse> createPayment(PayconiqPaymentRequest paymentRequest) {
        // S'assurer que le callbackUrl est défini
        if (paymentRequest.getCallbackUrl() == null) {
            paymentRequest.setCallbackUrl(callbackUrl);
        }

        return webClient.post()
                .uri("/payments")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-Merchant-Id", merchantId)
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PayconiqPaymentResponse.class);
    }

    @Override
    public Mono<PayconiqPaymentResponse> getPaymentDetails(String paymentId) {
        return webClient.get()
                .uri("/payments/" + paymentId)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(PayconiqPaymentResponse.class);
    }

    @Override
    public Mono<Void> cancelPayment(String paymentId) {
        return webClient.delete()
                .uri("/payments/" + paymentId)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(Void.class);
    }
}