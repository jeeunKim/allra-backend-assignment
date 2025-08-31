package com.allra.assignment.dev.order.model.entity;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.order.constant.OrderStatus;
import com.allra.assignment.dev.user.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails = new ArrayList<>();

        @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void setOrderId() {
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(Instant.now());

        int randomNum = new Random().nextInt(90000) + 10000; // 10000~99999

        this.orderId = timestamp + "_" + randomNum;
        this.createdAt = Instant.now();
    }

    @PreUpdate
    void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }


}