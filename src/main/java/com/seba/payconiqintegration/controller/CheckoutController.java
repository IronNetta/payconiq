package com.seba.payconiqintegration.controller;

import com.seba.payconiqintegration.model.PayconiqPaymentRequest;
import com.seba.payconiqintegration.model.PayconiqPaymentResponse;
import com.seba.payconiqintegration.service.PayconiqService;
import com.seba.payconiqintegration.service.inter.IPayconiqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final IPayconiqService payconiqService;

    @Autowired
    public CheckoutController(IPayconiqService payconiqService) {
        this.payconiqService = payconiqService;
    }

    @GetMapping
    public String showCheckoutPage(Model model) {
        // Ajouter des données nécessaires pour la page de paiement
        return "checkout";
    }

    @PostMapping("/pay")
    public String processPayment(@RequestParam BigDecimal amount, Model model) {
        // Création d'une requête de paiement Payconiq
        PayconiqPaymentRequest request = new PayconiqPaymentRequest();
        request.setAmount(amount);
        request.setCurrency("EUR");
        request.setDescription("Achat sur MonSite.com");
        request.setReference(UUID.randomUUID().toString());

        // Appel au service Payconiq pour créer un paiement
        PayconiqPaymentResponse response = payconiqService.createPayment(request).block();

        if (response != null) {
            // Ajouter les informations de paiement au modèle
            model.addAttribute("paymentId", response.getPaymentId());
            model.addAttribute("qrCode", response.getQrCode());
            model.addAttribute("deepLinkUrl", response.getDeepLinkQrCode());
            return "payment";
        } else {
            model.addAttribute("error", "Échec de la création du paiement");
            return "checkout";
        }
    }

    @GetMapping("/status/{paymentId}")
    @ResponseBody
    public String checkPaymentStatus(@PathVariable String paymentId) {
        // Vérifier le statut du paiement
        PayconiqPaymentResponse response = payconiqService.getPaymentDetails(paymentId).block();

        if (response != null) {
            return response.getStatus();
        } else {
            return "NOT_FOUND";
        }
    }
}
