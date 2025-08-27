package com.allra.assignment.dev.item.model.dto;

import com.allra.assignment.dev.item.model.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 DTO")
public class ItemDto {
    @Schema(description = "상품 고유번호")
    private Long id;

    @Schema(description = "상위 카테고리명")
    private String categoryName;

    @Schema(description = "하위 카테고리명")
    private String detailCategoryName;

    @Schema(description = "상품명")
    private String itemName;

    @Schema(description = "상품 정상가")
    private Long amount;

    @Schema(description = "상품 할인가")
    private Long discountAmount;

    @Schema(description = "상품 할인율")
    private Double discountRate;

    @Schema(description = "재고")
    private Long stock;

    @Schema(description = "품절 여부")
    private boolean isSoldOut;

    @Schema(description = "판매량")
    private Long numOfSales;

    @Schema(description = "상품 리뷰 평점")
    private Double reviewAvgRating;

    @Schema(description = "리뷰 수")
    private Long numOfRatings;


    public ItemDto(Item item) {
        this.id = item.getId();
        this.categoryName = item.getCategory().getCategoryName();
        this.detailCategoryName = item.getDetailCategory().getDetailCategoryName();
        this.itemName = item.getName();
        this.amount = item.getAmount();
        this.discountAmount = item.getDiscountAmount();
        this.discountRate = item.getDiscountRate();
        this.stock = item.getStock();
        this.isSoldOut = item.getIsSoldOut();
        this.numOfSales = item.getNumOfSales();
        this.reviewAvgRating = item.getReviewAvgRating();
        this.numOfRatings = item.getNumOfRatings();
    }
}
