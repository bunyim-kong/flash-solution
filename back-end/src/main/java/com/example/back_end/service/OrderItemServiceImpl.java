
package com.example.back_end.service;

import com.example.back_end.model.OrderItem;
import com.example.back_end.model.CustomerOrder;
import com.example.back_end.model.Product;

import com.example.back_end.repository.OrderItemRepository;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
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
                .orElseThrow(() -> new NoSuchElementException(
                        "Order item not found with ID: " + id
                ));
    }

    // CREATE
    @Override
    @Transactional
    public OrderItem createOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("Order item details are required");
        }

        Long orderId = requiredOrderId(orderItem.getCustomerOrder());

        CustomerOrder customerOrder = customerOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Customer order not found with ID: " + orderId
                ));

        Long productId = requiredProductId(orderItem.getProduct());

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product not found with ID: " + productId
                ));

        int quantity = positiveQuantity(orderItem.getQuantity());

        orderItem.setCustomerOrder(customerOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);

        // Snapshot trusted catalogue data instead of accepting prices from the client.
        orderItem.setProductSku(product.getSku());
        orderItem.setProductName(product.getName());
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setLineTotal(calculateLineTotal(product.getPrice(), quantity));

        return orderItemRepository.save(orderItem);
    }

    // UPDATE
    @Override
    @Transactional
    public OrderItem updateOrderItem(Long id, OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("Order item details are required");
        }

        OrderItem existingOrderItem = getOrderItemById(id);
        int quantity = positiveQuantity(orderItem.getQuantity());

        // Product and snapshot price are immutable during an item quantity update.
        existingOrderItem.setQuantity(quantity);
        existingOrderItem.setLineTotal(
                calculateLineTotal(existingOrderItem.getUnitPrice(), quantity)
        );

        return orderItemRepository.save(existingOrderItem);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem existingOrderItem = getOrderItemById(id);
        orderItemRepository.delete(existingOrderItem);
    }

    private Long requiredOrderId(CustomerOrder order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Customer order ID is required");
        }
        return order.getId();
    }

    private Long requiredProductId(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        return product.getId();
    }

    private int positiveQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return quantity;
    }

    private BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalStateException("Product price must be zero or greater");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
