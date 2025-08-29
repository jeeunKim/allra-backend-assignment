package com.allra.assignment.exception.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartItemErrorResult {

    ITEM_NOT_IN_CART(HttpStatus.NOT_FOUND, "장바구니에 존재하지 않는 상품입니다."),
    IS_SOLD_OUT_ITEM(HttpStatus.BAD_REQUEST, "상품이 품절되어 장바구니에 추가할 수 없습니다."),
    CANNOT_MODIFY_IS_SOLD_OUT(HttpStatus.BAD_REQUEST, "해당 상품은 품절되었습니다."),
    CANNOT_MODIFY_NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족하여 더 이상 수량을 추가할 수 없습니다."),
    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "재고 수량이 부족하여 장바구니에 추가할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
