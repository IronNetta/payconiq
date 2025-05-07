package com.seba.payconiqintegration.service.inter;

import com.seba.payconiqintegration.model.PayconiqPaymentRequest;
import com.seba.payconiqintegration.model.PayconiqPaymentResponse;
import reactor.core.publisher.Mono;

public interface IPayconiqService {
    Mono<PayconiqPaymentResponse> createPayment(PayconiqPaymentRequest paymentRequest);
    Mono<PayconiqPaymentResponse> getPaymentDetails(String paymentId);
    Mono<Void> cancelPayment(String paymentId);
}