package com.allra.assignment.dev.order.model.response;

import com.allra.assignment.dev.order.constant.OrderStatus;
import com.allra.assignment.dev.order.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String orderId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Long paymentAmount;
    private String transactionId;
    private String message;
    private String failReason;
    private List<OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private Long amount;
    }
}
