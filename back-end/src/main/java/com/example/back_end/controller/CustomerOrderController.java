package com.example.back_end.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.service.CustomerOrderService;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    // create method
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@RequestBody CustomerOrder customerOrder) {
        return customerOrderService.createCustomerOrder(customerOrder);
    }

    // get method to get all
    @GetMapping
    public List<CustomerOrder> getAll() {
        return customerOrderService.getAllCustomerOrders();
    }

    // get method to get one
    @GetMapping("/{id}")
    public CustomerOrder getById(@PathVariable Long id) {
        return customerOrderService.getCustomerOrderById(id);
    }

    // update method
    @PutMapping("/{id}")
    public CustomerOrder update(@PathVariable Long id, @RequestBody CustomerOrder customerOrder) {
        return customerOrderService.updateCustomerOrder(id, customerOrder);
    }

    // delete method
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerOrderService.deleteCustomerOrder(id);
    }
}
