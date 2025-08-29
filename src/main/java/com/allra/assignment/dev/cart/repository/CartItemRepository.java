package com.allra.assignment.dev.cart.repository;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserUserIdAndItemItemId(Long userId, Long itemId);

    List<CartItem> findByUserUserId(Long userId);

    void deleteByUserUserIdAndItemItemId(Long userId, Long itemId);

    void deleteAllByUser(User user);
}