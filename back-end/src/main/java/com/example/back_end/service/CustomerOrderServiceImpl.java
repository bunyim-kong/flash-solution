package com.example.back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.repository.CustomerOrderRepository;

@Service 
public class CustomerOrderServiceImpl implements  CustomerOrderService{

    private final CustomerOrderRepository repository;
    
    public CustomerOrderServiceImpl(CustomerOrderRepository repository){
        this.repository = repository; 
    }

    //read
    @Override 
    public List<CustomerOrder> getAllOrder(){
        return repository.findAll();
    }

    //create
    @Override 
    public CustomerOrder createOrder(CustomerOrder order){
        return repository.save(order);
    }

}
