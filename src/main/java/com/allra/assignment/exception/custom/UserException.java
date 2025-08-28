package com.allra.assignment.exception.custom;


import com.allra.assignment.exception.result.UserErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final UserErrorResult errorResult;

}
