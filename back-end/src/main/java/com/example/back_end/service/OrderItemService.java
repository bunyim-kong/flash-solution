
package com.example.back_end.service;

import com.example.back_end.model.OrderItem;
import java.util.List;

public interface OrderItemService {

    // Get all order items
    List<OrderItem> getAllOrderItems();

    // Get one order item by ID
    OrderItem getOrderItemById(Long id);

    // Create a new order item
    OrderItem createOrderItem(OrderItem orderItem);

    // Update an existing order item
    OrderItem updateOrderItem(Long id, OrderItem orderItem);

    // Delete an order item
    void deleteOrderItem(Long id);
}
