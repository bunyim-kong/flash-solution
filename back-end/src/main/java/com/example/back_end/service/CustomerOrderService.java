package com.example.back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.back_end.model.CustomerOrder;

@Service 
public interface CustomerOrderService {
    List<CustomerOrder> getAllOrder();

    CustomerOrder createOrder();
}
