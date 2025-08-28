package com.allra.assignment.dev.cart.model.response;


import com.allra.assignment.dev.cart.model.dto.MyItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "장바구니 조회 응답")
public class CartItemResponse {

    @Schema(description = "유저 아이디")
    private long userId;

    @Schema(description = "총 금액")
    private long totalPrice;

    @Schema(description = "총 개수")
    private int totalQuantity;

    @Schema(description = "장바구니 상품, 카테고리별로 매핑")
    Map<Long, List<MyItemDto>> myItems = new HashMap<>();

}
