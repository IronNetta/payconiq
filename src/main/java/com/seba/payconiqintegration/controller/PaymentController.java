package com.seba.payconiqintegration.controller;

import com.seba.payconiqintegration.model.PayconiqPaymentRequest;
import com.seba.payconiqintegration.model.PayconiqPaymentResponse;
import com.seba.payconiqintegration.service.PayconiqService;
import com.seba.payconiqintegration.service.inter.IPayconiqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPayconiqService payconiqService;

    @Autowired
    public PaymentController(IPayconiqService payconiqService) {
        this.payconiqService = payconiqService;
    }

    /**
     * Endpoint pour créer un nouveau paiement
     */
    @PostMapping
    public Mono<ResponseEntity<PayconiqPaymentResponse>> createPayment(@RequestBody PayconiqPaymentRequest request) {
        return payconiqService.createPayment(request)
                .map(ResponseEntity::ok);
    }

    /**
     * Endpoint pour obtenir les détails d'un paiement
     */
    @GetMapping("/{paymentId}")
    public Mono<ResponseEntity<PayconiqPaymentResponse>> getPayment(@PathVariable String paymentId) {
        return payconiqService.getPaymentDetails(paymentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour annuler un paiement
     */
    @DeleteMapping("/{paymentId}")
    public Mono<ResponseEntity<Void>> cancelPayment(@PathVariable String paymentId) {
        return payconiqService.cancelPayment(paymentId)
                .thenReturn(ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}