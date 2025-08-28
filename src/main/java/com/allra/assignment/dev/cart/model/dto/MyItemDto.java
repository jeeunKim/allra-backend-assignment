package com.allra.assignment.dev.cart.model.dto;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.item.model.dto.ItemAmountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.net.URI;

@Data
@Schema(description = "내 장바구니 상품")
public class MyItemDto {
    @Schema(description = "장바구니 고유번호")
    private Long cartItemId;

    @Schema(description = "상품 고유번호")
    private Long itemId;

    @Schema(description = "상품명")
    private String itemName;

    @Schema(description = "상품 디테일 카테고리")
    private Integer detailCategoryId;

    @Schema(description = "품절 여부")
    private boolean isSoldOut;

    @Schema(description = "수량")
    private int quantity;

    @Schema(description = "담을 당시 금액")
    private ItemAmountDto amountAtAdded;

    @Schema(description = "현재 상품의 금액")
    private ItemAmountDto amountNow;

    @Schema(description = "상품 이미지")
    private String itemImage;

    @Schema(description = "상품 이미지 경로")
    private String itemImagePath;

    @Schema(description = "상품 조회 URI")
    private URI itemLocation;

    public MyItemDto(CartItem cartItem) {
        this.cartItemId = cartItem.getCartItemId();
        this.itemId = cartItem.getItem().getItemId();
        this.itemName = cartItem.getItem().getName();
        this.detailCategoryId = cartItem.getItem().getDetailCategory().getDetailCategoryId();
        this.isSoldOut = cartItem.getItem().getIsSoldOut();
        this.quantity = cartItem.getQuantity();
        this.itemImage = cartItem.getItem().getItemImage();
        this.itemImagePath = cartItem.getItem().getItemImagePath();

    }
}
