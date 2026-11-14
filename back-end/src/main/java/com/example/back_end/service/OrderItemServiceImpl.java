package com.example.back_end.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.OrderItem;
import com.example.back_end.repository.OrderItemRepository;

@Service
@Transactional(readOnly = true)
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    // function create
    @Override
    @Transactional
    public OrderItem createOrderItem(OrderItem orderItem) {
        orderItem.setId(null);
        orderItem.setCustomerOrder(required(orderItem.getCustomerOrder(), "Customer order"));
        orderItem.setProduct(required(orderItem.getProduct(), "Product"));
        orderItem.setQuantity(positive(orderItem.getQuantity(), "Order item quantity"));
        orderItem.setProductSku(text(orderItem.getProductSku(), "Product sku"));
        orderItem.setProductName(text(orderItem.getProductName(), "Product name"));
        orderItem.setUnitPrice(nonNegative(orderItem.getUnitPrice(), "Unit price"));
        orderItem.setLineTotal(nonNegative(orderItem.getLineTotal(), "Line total"));
        return orderItemRepository.save(orderItem);
    }

    // function read
    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAllByOrderByIdAsc();
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Order Item with Id " + id + " was not found."));
    }

    // function update
    @Override
    @Transactional
    public OrderItem updateOrderItem(Long id, OrderItem orderItem) {
        OrderItem existing = getOrderItemById(id);

        existing.setCustomerOrder(required(orderItem.getCustomerOrder(), "Customer order"));
        existing.setProduct(required(orderItem.getProduct(), "Product"));
        existing.setQuantity(positive(orderItem.getQuantity(), "Order item quantity"));
        existing.setProductSku(text(orderItem.getProductSku(), "Product sku"));
        existing.setProductName(text(orderItem.getProductName(), "Product name"));
        existing.setUnitPrice(nonNegative(orderItem.getUnitPrice(), "Unit price"));
        existing.setLineTotal(nonNegative(orderItem.getLineTotal(), "Line total"));
        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        orderItemRepository.delete(getOrderItemById(id));
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value.trim();
    }

    private <T> T required(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value;
    }

    private Integer positive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero.");
        }
        return value;
    }

    private BigDecimal nonNegative(BigDecimal value, String field) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return value;
    }
}
