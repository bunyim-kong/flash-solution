package com.example.back_end.service;

import java.util.List;

import com.example.back_end.model.CustomerOrder;

public interface CustomerOrderService {

    CustomerOrder createCustomerOrder(CustomerOrder customerOrder);

    List<CustomerOrder> getAllCustomerOrders();

    CustomerOrder getCustomerOrderById(Long id);

    CustomerOrder updateCustomerOrder(Long id, CustomerOrder customerOrder);

    void deleteCustomerOrder(Long id);
}
