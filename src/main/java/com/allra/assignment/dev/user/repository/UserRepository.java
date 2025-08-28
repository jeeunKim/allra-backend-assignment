package com.allra.assignment.dev.user.repository;

import com.allra.assignment.dev.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}