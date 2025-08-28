package com.allra.assignment.exception.custom;


import com.allra.assignment.exception.result.CartItemErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartItemException extends RuntimeException {

    private final CartItemErrorResult errorResult;

}
