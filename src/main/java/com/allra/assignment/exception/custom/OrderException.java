package com.allra.assignment.exception.custom;


import com.allra.assignment.exception.result.OrderErrorResult;
import com.allra.assignment.exception.result.UserErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class OrderException extends RuntimeException {

    private final OrderErrorResult errorResult;
    private final String detailMessage;

    public OrderException(OrderErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
        this.detailMessage = null;
    }

    public OrderException(OrderErrorResult errorResult, String detailMessage) {
        super(errorResult.getMessage() + (detailMessage != null ? "\n" + detailMessage : ""));
        this.errorResult = errorResult;
        this.detailMessage = detailMessage;
    }
}
