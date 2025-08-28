package com.allra.assignment.exception.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartItemErrorResult {

    IS_SOLD_OUT_ITEM(HttpStatus.BAD_REQUEST, "상품이 품절되어 장바구니에 추가할 수 없습니다."),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고 수량이 부족하여 장바구니에 추가할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
