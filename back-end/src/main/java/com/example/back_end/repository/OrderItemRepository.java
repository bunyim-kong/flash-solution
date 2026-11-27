package com.example.back_end.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByCustomerOrderIdInOrderById(Collection<Long> orderIds);

    boolean existsByProductId(Long productId);
}
