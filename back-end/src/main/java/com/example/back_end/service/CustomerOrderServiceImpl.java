package com.example.back_end.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.repository.CustomerOrderRepository;

@Service
@Transactional(readOnly = true)
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderServiceImpl(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    // function create
    @Override
    @Transactional
    public CustomerOrder createCustomerOrder(CustomerOrder customerOrder) {
        customerOrder.setId(null);
        customerOrder.setOrderNumber(text(customerOrder.getOrderNumber(), "Order number"));
        customerOrder.setStatus(text(customerOrder.getStatus(), "Order status"));
        customerOrder.setTotalAmount(nonNegative(customerOrder.getTotalAmount(), "Order total amount"));
        customerOrder.setCustomerName(text(customerOrder.getCustomerName(), "Customer name"));
        customerOrder.setCustomerEmail(text(customerOrder.getCustomerEmail(), "Customer email"));
        customerOrder.setPhoneNumber(text(customerOrder.getPhoneNumber(), "Customer phone number"));
        customerOrder.setAddress(text(customerOrder.getAddress(), "Customer address"));
        customerOrder.setMapLink(optionalText(customerOrder.getMapLink()));
        customerOrder.setPaymentMethod(text(customerOrder.getPaymentMethod(), "Payment method"));

        checkUnique(customerOrder.getOrderNumber(), null);
        return customerOrderRepository.save(customerOrder);
    }

    // function read
    @Override
    public List<CustomerOrder> getAllCustomerOrders() {
        return customerOrderRepository.findAllByOrderByIdDesc();
    }

    @Override
    public CustomerOrder getCustomerOrderById(Long id) {
        return customerOrderRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Customer Order with Id " + id + " was not found."));
    }

    // function update
    @Override
    @Transactional
    public CustomerOrder updateCustomerOrder(Long id, CustomerOrder customerOrder) {
        CustomerOrder existing = getCustomerOrderById(id);
        String orderNumber = text(customerOrder.getOrderNumber(), "Order number");

        checkUnique(orderNumber, existing.getOrderNumber());

        existing.setOrderNumber(orderNumber);
        existing.setUser(customerOrder.getUser());
        existing.setStatus(text(customerOrder.getStatus(), "Order status"));
        existing.setTotalAmount(nonNegative(customerOrder.getTotalAmount(), "Order total amount"));
        existing.setCustomerName(text(customerOrder.getCustomerName(), "Customer name"));
        existing.setCustomerEmail(text(customerOrder.getCustomerEmail(), "Customer email"));
        existing.setPhoneNumber(text(customerOrder.getPhoneNumber(), "Customer phone number"));
        existing.setAddress(text(customerOrder.getAddress(), "Customer address"));
        existing.setMapLink(optionalText(customerOrder.getMapLink()));
        existing.setPaymentMethod(text(customerOrder.getPaymentMethod(), "Payment method"));
        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteCustomerOrder(Long id) {
        customerOrderRepository.delete(getCustomerOrderById(id));
    }

    private void checkUnique(String orderNumber, String oldOrderNumber) {
        if (!orderNumber.equals(oldOrderNumber)
                && customerOrderRepository.existsByOrderNumberIgnoreCase(orderNumber)) {
            throw new IllegalArgumentException("A Customer Order with " + orderNumber + " already exist.");
        }
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value.trim();
    }

    private String optionalText(String value) {
        return value == null ? null : value.trim();
    }

    private BigDecimal nonNegative(BigDecimal value, String field) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return value;
    }
}
