package com.allra.assignment.dev.order.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatus {

    SUCCESS("배송 완료"),
    FAILED("구매 확정");


    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public static PaymentStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
