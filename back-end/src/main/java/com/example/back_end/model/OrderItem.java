package com.example.back_end.model;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private BigDecimal price;

    // Relationship with Order
    @ManyToOne
    @JoinColumn(name = "customer_order_id")
    private CustomerOrder CustomerOrder;

    // Relationship with Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CustomerOrder getCustomerOrder() {
        return CustomerOrder;
    }

    public void setCustomerOrder(CustomerOrder CustomerOrder) {
        this.CustomerOrder = CustomerOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
