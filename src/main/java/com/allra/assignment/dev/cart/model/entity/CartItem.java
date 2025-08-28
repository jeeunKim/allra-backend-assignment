package com.allra.assignment.dev.cart.model.entity;

import com.allra.assignment.dev.item.model.entity.Item;
import com.allra.assignment.dev.user.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "cart_items")
/**
 * 인덱스
 *      단일 인덱스 User_id
 */
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Long cartItemId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @NotNull
    @Column(name = "total_discount_amount", nullable = false)
    private Long totalDiscountAmount;

    @NotNull
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

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