
package com.example.back_end.controller;

import com.example.back_end.model.OrderItem;
import com.example.back_end.service.OrderItemService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    // Constructor Injection
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // GET ALL ORDER ITEMS
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(
                orderItemService.getAllOrderItems()
        );
    }

    // GET ORDER ITEM BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderItemService.getOrderItemById(id)
        );
    }

    // CREATE ORDER ITEM
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(
            @RequestBody OrderItem orderItem) {

        OrderItem savedOrderItem =
                orderItemService.createOrderItem(orderItem);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedOrderItem);
    }

    // UPDATE ORDER ITEM
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(
            @PathVariable Long id,
            @RequestBody OrderItem orderItem) {

        return ResponseEntity.ok(
                orderItemService.updateOrderItem(id, orderItem)
        );
    }

    // DELETE ORDER ITEM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return ResponseEntity.noContent().build();
    }
}
