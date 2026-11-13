package com.example.back_end.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String order_number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal total_amount;

    @Column (name="customer_name")
    private String customer_name;

    @Column (name="customer_email")
    private String customer_email;

    @Column(name = "phone_number")
    private String phone_number;

    private String address;

    @Column (name="map_link")
    private String map_link;

    @Column (name="payment_method")
    private String payment_method;

    public Long getId() {
        return id;
    }

    @Column (name="order_number")
    public String getOrderNumber() {
        return order_number;
    }

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return total_amount;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public String getCustomerEmail() {
        return customer_email;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getAddress() {
        return address;
    }

    public String getMapLink() {
        return map_link;
    }

    public String getPaymentMethod() {
        return payment_method;
    }
}
