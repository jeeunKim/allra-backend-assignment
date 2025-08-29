package com.allra.assignment.dev.order.model.entity;

import com.allra.assignment.dev.item.model.entity.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @Column(name = "order_detail_id", nullable = false)
    private String orderDetailId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Long amount;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    @PrePersist
    void setOrderId() {
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(Instant.now());

        int randomNum = new Random().nextInt(90000) + 10000; // 10000~99999

        this.orderDetailId = timestamp + "_" + randomNum;
    }


}