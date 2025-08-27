package com.allra.assignment.dev.item.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
/**
 * 인덱스
 * README 단일 인덱스 category_id
 *        단일 인덱스 detail_category_id
 *        고려 인덱스: review_avg_rating, 소수점 한자리로 0.0 ~ 5.0까지로 데이터 관리시 50가지이므로 평점 순 검색 등에서 용이할 가능성 있음
 *
 * 가격 컬럼 인덱스는 카디널리티가 높아 비효율적일 것으로 예상됨
 */
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Category category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "detail_category_id", nullable = false, insertable = false, updatable = false)
    private DetailCategory detailCategory;

    @Size(max = 200)
    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull
    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @NotNull
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate;

    @NotNull
    @Column(name = "stock", nullable = false)
    private Long stock;

    @NotNull
    @Column(name = "is_sold_out", nullable = false)
    private Boolean isSoldOut = false;

    @NotNull
    @Column(name = "num_of_sales", nullable = false)
    private Long numOfSales;

    @NotNull
    @Column(name = "review_avg_rating", nullable = false)
    private Double reviewAvgRating;

    @NotNull
    @Column(name = "num_of_ratings", nullable = false)
    private Long numOfRatings;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


    @PrePersist
    void setCreatedAt() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    void setUpdatedAt() {
        this.createdAt = Instant.now();
    }

}