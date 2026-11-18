package com.example.back_end.service;

import java.util.List;

import com.example.back_end.model.CustomerOrder;

public interface CustomerOrderService {
    List<CustomerOrder> getAllOrder();

    CustomerOrder findCustomerOrderById(Long id);

    CustomerOrder updateCustmOrder(Long id, CustomerOrder customerOrder);

    void deleteById(Long id);

    CustomerOrder createCustomerOrder(CustomerOrder customerOrder, Long userId);

}
