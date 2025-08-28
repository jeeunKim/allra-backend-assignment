package com.allra.assignment.dev.cart.repository;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserUserIdAndItemItemId(long userId, long itemId);
}