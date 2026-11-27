package com.example.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back_end.model.CustomerOrder;

public interface  CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>   {
    boolean existsByUserId(Long userId);

    List<CustomerOrder> findAllByUserIdOrderByIdDesc(Long userId);

    boolean existsByOrderNumberIgnoreCase(String orderNumber);

    Optional<CustomerOrder> findByOrderNumberIgnoreCase(String orderNumber);
}
