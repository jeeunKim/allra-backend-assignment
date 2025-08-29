package com.allra.assignment.dev.order.model.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private Long amount;

    public PaymentRequest(String orderId, Long amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
