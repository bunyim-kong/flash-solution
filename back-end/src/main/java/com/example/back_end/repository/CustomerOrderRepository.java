package com.example.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back_end.model.CustomerOrder;

public interface  CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>   {
    boolean existsByOrderNumberIgnoreCase(String orderNumber);

    Optional<CustomerOrder> findByOrderNumberIgnoreCase(String orderNumber);
}
