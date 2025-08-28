package com.allra.assignment.dev.item.model.dto;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.item.model.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "상품 금액 DTO")
public class ItemAmountDto {
    @Schema(description = "금액")
    private Long totalAmount;

    @Schema(description = "할인된 금액")
    private Long totalDiscountAmount;

    @Schema(description = "할인율")
    private Double discountRate;

    public ItemAmountDto(CartItem cartItem) {
        this.totalAmount = cartItem.getTotalAmount();
        this.totalDiscountAmount = cartItem.getTotalDiscountAmount();
        this.discountRate = cartItem.getDiscountRate();
    }
    public ItemAmountDto(Item item, int quantity) {
        this.totalAmount = item.getAmount() * quantity;
        this.totalDiscountAmount = item.getDiscountAmount() * quantity;
        this.discountRate = item.getDiscountRate();
    }
}
