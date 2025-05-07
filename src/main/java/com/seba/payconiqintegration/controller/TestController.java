package com.seba.payconiqintegration.controller;

import com.seba.payconiqintegration.service.MockPayconiqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Profile("test")
public class TestController {

    private final MockPayconiqService mockPayconiqService;

    @Autowired
    public TestController(MockPayconiqService mockPayconiqService) {
        this.mockPayconiqService = mockPayconiqService;
    }

    @PostMapping("/payment/{paymentId}/succeed")
    public ResponseEntity<String> simulateSuccessfulPayment(@PathVariable String paymentId) {
        mockPayconiqService.simulateSuccessfulPayment(paymentId);
        return ResponseEntity.ok("Payment " + paymentId + " marked as successful");
    }

    @PostMapping("/payment/{paymentId}/fail")
    public ResponseEntity<String> simulateFailedPayment(@PathVariable String paymentId) {
        mockPayconiqService.simulateFailedPayment(paymentId);
        return ResponseEntity.ok("Payment " + paymentId + " marked as failed");
    }
}