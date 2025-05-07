package com.seba.payconiqintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayconiqPaymentResponse {
    private String paymentId;
    private BigDecimal amount;
    private String description;
    private String status;
    private String createdAt;
    private String completedAt;
    private String deepLinkQrCode;
    private String qrCode;
}
