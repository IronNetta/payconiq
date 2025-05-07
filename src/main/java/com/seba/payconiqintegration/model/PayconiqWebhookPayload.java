package com.seba.payconiqintegration.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Modèle représentant la charge utile des webhooks Payconiq
 * Cette classe contient toutes les données importantes envoyées par Payconiq lors d'une notification webhook
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayconiqWebhookPayload {

    @JsonProperty("paymentId")
    private String paymentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("description")
    private String description;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant updatedAt;

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("posId")
    private String posId;

    @JsonProperty("transferId")
    private String transferId;

    @JsonProperty("callbackUrl")
    private String callbackUrl;

    @JsonProperty("deeplinkUrl")
    private String deeplinkUrl;

    @JsonProperty("qrcode")
    private String qrcode;

    @JsonProperty("details")
    private PaymentDetails details;

    /**
     * Classe interne représentant les détails supplémentaires du paiement
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentDetails {
        @JsonProperty("cardNumber")
        private String cardNumber;

        @JsonProperty("cardHolder")
        private String cardHolder;

        @JsonProperty("bankAccount")
        private String bankAccount;

        @JsonProperty("bankName")
        private String bankName;

        @JsonProperty("reason")
        private String reason;

        @JsonProperty("errorCode")
        private String errorCode;

    }
}