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

    @Column(name = "order_number", nullable = false)
    private String order_number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal total_amount;

    @Column (name="customer_name", nullable = false, length = 30)
    private String customer_name;

    @Column (name="customer_email", nullable = false)
    private String customer_email;

    @Column(name = "phone_number", length = 10, nullable = false)
    private String phone_number;

    @Column (nullable = false)
    private String address;

    @Column (name="map_link", nullable = false)
    private String map_link;

    @Column (name="payment_method", nullable = false)
    private String payment_method;

    public Long getId() {
        return id;
    }

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

    public void setId(Long id){
        this.id = id;
    }
    
    public void setOrderNumber(String order_number){
        this.order_number = order_number;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setTotalAmount(BigDecimal total_amount){
        this.total_amount = total_amount;
    }

    public void setCustomerName(String customer_name){
        this.customer_name = customer_name;
    }

    public void setCustomerEmail(String customer_email){
        this.customer_email = customer_email;
    }

    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setMapLink(String map_link){
        this.map_link = map_link;
    }

    public void setPaymentMethod(String payment_method){
        this.payment_method = payment_method;
    }
}
