package com.example.back_end.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.service.CustomerOrderService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    
    @PostMapping
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody CustomerOrder order){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.createOrder(order));
    }
    
}
