package com.allra.assignment.dev.order.model.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String status;
    private String transactionId;
    private String message;

    public PaymentResponse(String status, String transactionId, String message) {
        this.status = status;
        this.transactionId = transactionId;
        this.message = message;
    }
}
