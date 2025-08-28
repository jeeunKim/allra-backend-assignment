package com.allra.assignment.exception.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ItemErrorResult {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    INVALID_AMOUNT_RANGE(HttpStatus.BAD_REQUEST, "최소 금액이 최대 금액보다 클 수 없습니다."),
    INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "정렬 기능을 지원하지 않는 항목입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
