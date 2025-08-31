package com.allra.assignment.exception.custom;


import com.allra.assignment.exception.result.ItemErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemException extends RuntimeException {

    private final ItemErrorResult errorResult;

}
