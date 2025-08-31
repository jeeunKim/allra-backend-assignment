package com.allra.assignment.exception;

import com.allra.assignment.exception.custom.CartItemException;
import com.allra.assignment.exception.custom.ItemException;
import com.allra.assignment.exception.custom.OrderException;
import com.allra.assignment.exception.custom.UserException;
import com.allra.assignment.exception.result.CartItemErrorResult;
import com.allra.assignment.exception.result.ItemErrorResult;
import com.allra.assignment.exception.result.OrderErrorResult;
import com.allra.assignment.exception.result.UserErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 유효성 검사 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {

        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorList.toString()));
    }

    // 상품 관련 예외
    @ExceptionHandler({ItemException.class})
    public ResponseEntity<ErrorResponse> handleCustomItemException(final ItemException exception) {
        log.warn("ItemException error occured: ", exception);
        return this.makeItemErrorResponseEntity(exception.getErrorResult());
    }

    // 장바구니 관련 예외
    @ExceptionHandler({CartItemException.class})
    public ResponseEntity<ErrorResponse> handleCustomCartItemException(final CartItemException exception) {
        log.warn("CartItemException error occured: ", exception);
        return this.makeCartItemErrorResponseEntity(exception.getErrorResult());
    }

    // 회원관련 예외
    @ExceptionHandler({UserException.class})
    public ResponseEntity<ErrorResponse> handleCustomUserException(final UserException exception) {
        log.warn("UserException error occured: ", exception);
        return this.makeUserErrorResponseEntity(exception.getErrorResult());
    }

    // 결제 관련 예외
    @ExceptionHandler({OrderException.class})
    public ResponseEntity<ErrorResponse> handleCustomOrderException(final OrderException exception) {
        log.warn("OderException error occured: ", exception);
        String message = exception.getDetailMessage() != null
                ? exception.getErrorResult().getMessage()+ exception.getDetailMessage()
                : exception.getErrorResult().getMessage();

        return ResponseEntity.status(exception.getErrorResult().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorResult().name(), message));
    }


    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSort(InvalidDataAccessApiUsageException exception) {
        log.warn("ItemException error occured: ", exception);
        return makeItemErrorResponseEntity(ItemErrorResult.INVALID_SORT_FIELD);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
        log.warn("Exception error occured: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(null, "알 수 없는 에러가 발생했습니다. 다시 시도해주세요."));
    }

    private ResponseEntity<ErrorResponse> makeItemErrorResponseEntity(final ItemErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }
    private ResponseEntity<ErrorResponse> makeCartItemErrorResponseEntity(final CartItemErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    private ResponseEntity<ErrorResponse> makeUserErrorResponseEntity(final UserErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }



}
