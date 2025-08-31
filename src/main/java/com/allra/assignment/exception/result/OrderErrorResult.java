package com.allra.assignment.exception.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorResult {

    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "상품의 재고가 부족합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
