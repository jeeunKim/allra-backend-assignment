package com.allra.assignment.dev.order.repository;

import com.allra.assignment.dev.order.model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}