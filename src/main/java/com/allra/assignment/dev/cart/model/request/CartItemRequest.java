package com.allra.assignment.dev.cart.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;

@Data
public class CartItemRequest {

    @Schema(description = "상품 고유번호")
    @NotNull(message = "장바구니에 담을 상품을 선택하십시오.")
    private long itemId;

    @Schema(description = "유저 아이디")
    @NotNull(message = "회원 정보를 확인할 수 없습니다. 다시 시도해주세요.")
    private long userId; // TODO 인증 체계 추가

    @Schema(description = "상품 수량")
    @NotNull(message = "장바구니에 담을 상품의 수량을 선택하십시오.")
    @Positive(message = "1개 이상의 수량을 선택하십시오.")
    private int quantity;


}
