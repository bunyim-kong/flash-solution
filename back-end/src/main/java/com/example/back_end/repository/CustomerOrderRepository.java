package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back_end.model.CustomerOrder;
import java.util.List;

public interface  CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>   {
    boolean existsByOrderNumberIgnoreCase(String orderNumber);

    List<CustomerOrder> findAllByOrderByIdDesc();
}
