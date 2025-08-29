package com.allra.assignment.dev.order.repository;

import com.allra.assignment.dev.order.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}