
package com.example.back_end.service;

import com.example.back_end.model.OrderItem;
import com.example.back_end.model.CustomerOrder;
import com.example.back_end.model.Product;

import com.example.back_end.repository.OrderItemRepository;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.math.BigDecimal;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    // Constructor Injection
    public OrderItemServiceImpl(
            OrderItemRepository orderItemRepository,
            CustomerOrderRepository customerOrderRepository,
            ProductRepository productRepository) {

        this.orderItemRepository = orderItemRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.productRepository = productRepository;
    }

    // GET ALL
    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // GET BY ID
    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order item not found with ID: " + id
                ));
    }

    // CREATE
    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {

        // Find existing customer order
        Long orderId = orderItem.getCustomerOrder().getId();

        CustomerOrder customerOrder = customerOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer order not found"
                ));

        // Find existing product
        Long productId = orderItem.getProduct().getId();

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        // Set relationships
        orderItem.setCustomerOrder(customerOrder);
        orderItem.setProduct(product);

        // Calculate line total
        BigDecimal lineTotal = orderItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        orderItem.setLineTotal(lineTotal);

        // Save
        return orderItemRepository.save(orderItem);
    }

    // UPDATE
    @Override
    public OrderItem updateOrderItem(Long id, OrderItem orderItem) {

        OrderItem existingOrderItem = getOrderItemById(id);

        existingOrderItem.setQuantity(orderItem.getQuantity());
        existingOrderItem.setProductSku(orderItem.getProductSku());
        existingOrderItem.setProductName(orderItem.getProductName());
        existingOrderItem.setUnitPrice(orderItem.getUnitPrice());

        existingOrderItem.setLineTotal(
                orderItem.getUnitPrice().multiply(
                        BigDecimal.valueOf(orderItem.getQuantity())
                )
        );

        return orderItemRepository.save(existingOrderItem);
    }

    // DELETE
    @Override
    public void deleteOrderItem(Long id) {
        OrderItem existingOrderItem = getOrderItemById(id);
        orderItemRepository.delete(existingOrderItem);
    }
}
