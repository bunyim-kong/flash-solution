package com.example.back_end.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.service.CustomerOrderService;



@RestController 
@RequestMapping("/api/customer-orders")
public class CustomerOrderController {

    private final CustomerOrderService service;

    public CustomerOrderController(CustomerOrderService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrder>> getAllOrders(){
        return ResponseEntity.ok(service.getAllOrder());
    }
    
}
