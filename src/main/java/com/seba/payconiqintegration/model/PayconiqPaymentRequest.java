package com.seba.payconiqintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayconiqPaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String description;
    private String reference;
    private String callbackUrl;

}