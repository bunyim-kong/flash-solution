package com.example.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //fuction get all data 
    @GetMapping
    public List<CustomerOrder> getAllOrders(){
        return service.getAllOrder();
    }
    //

    //fuction get data by id 
    @GetMapping("/{id}")
    public CustomerOrder findCustomerOrderById(@PathVariable Long id){
        return service.findCustomerOrderById(id);
    }
    // 

    // fuction delete 
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "Order deleted successfully";
    }
    //


    //fuction update 
    @PutMapping("/{id}")
    public CustomerOrder  updateCustmOrder(@PathVariable  Long id, @RequestBody CustomerOrder customerOrder){
        return service.updateCustmOrder(id, customerOrder);
    }
    //

    //fucntion create
    @PostMapping
    public CustomerOrder createCustomerOrder(@RequestBody CustomerOrder customerOrder, @RequestParam Long userId){
        return service.createCustomerOrder(customerOrder, userId);
    }
    //

}
