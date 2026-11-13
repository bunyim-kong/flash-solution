package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back_end.model.CustomerOrder;

public interface  CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>   {
    
}
