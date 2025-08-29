package com.allra.assignment.dev.order.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {

    AWAITING_PAYMENT("결제 대기"),
    PAYMENT_SUCCESS("결제 완료"),
    PAYMENT_FAILED("결제 실패"),
    PREPARING("상품 준비중"),
    READY_FOR_SHIPMENT("배송 준비 완료"),
    SHIPPED("배송 시작"),
    IN_TRANSIT("배송중"),
    DELIVERED("배송 완료"),
    COMPLETED("구매 확정");


    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
