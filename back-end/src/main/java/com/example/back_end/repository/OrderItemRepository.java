package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}