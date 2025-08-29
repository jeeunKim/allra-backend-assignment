package com.allra.assignment.dev.order.repository;

import com.allra.assignment.dev.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}