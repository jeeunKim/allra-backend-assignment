package com.allra.assignment.dev.exception.custom;


import com.allra.assignment.dev.exception.ItemErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemException extends RuntimeException {

    private final ItemErrorResult errorResult;

}
