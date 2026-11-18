package com.example.back_end.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.model.User;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class CustomerOrderServiceImpl implements  CustomerOrderService{

    private final CustomerOrderRepository repository;
    private final UserRepository userRepository;

    public CustomerOrderServiceImpl(CustomerOrderRepository repository, UserRepository userRepository){
        this.repository = repository;
        this.userRepository = userRepository;
    }

    //get all data from database
    @Override 
    public List<CustomerOrder> getAllOrder(){
        return repository.findAll();
    }

    //find data by id
    @Override
    @Transactional
    public CustomerOrder findCustomerOrderById(Long id){
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Order " + id + " was not found"));
    }

    //create data
    @Override
    @Transactional
    public CustomerOrder createCustomerOrder(CustomerOrder customerOrder, Long userId){
        if (customerOrder == null) {
            throw new IllegalArgumentException("Order details are required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User " + userId + " was not found"));
        customerOrder.setId(null);
        customerOrder.setUser(user);

        String order_number = text(customerOrder.getOrderNumber());
        if (repository.existsByOrderNumberIgnoreCase(order_number)) {
            throw new IllegalArgumentException("Order number '" + order_number + "' already exists");
        }
        customerOrder.setOrderNumber(order_number);
        customerOrder.setStatus(text(customerOrder.getStatus()));
        customerOrder.setTotalAmount(decimal(customerOrder.getTotalAmount()));
        customerOrder.setCustomerName(text(customerOrder.getCustomerName()));
        customerOrder.setCustomerEmail(text(customerOrder.getCustomerEmail()));
        customerOrder.setPhoneNumber(text(customerOrder.getPhoneNumber()));
        customerOrder.setAddress(text(customerOrder.getAddress()));
        customerOrder.setMapLink(text(customerOrder.getMapLink()));
        customerOrder.setPaymentMethod(text(customerOrder.getPaymentMethod()));

        return repository.save(customerOrder);
    }

    //delete the data by id
    @Override
    @Transactional
    public void deleteById(Long id ){
        repository.delete(findCustomerOrderById(id));
    }

    @Override
    @Transactional
    public CustomerOrder updateCustmOrder(Long id, CustomerOrder customerOrder){

        CustomerOrder existing = findCustomerOrderById(id);

        String order_number = text(customerOrder.getOrderNumber());
        repository.findByOrderNumberIgnoreCase(order_number)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Order number '" + order_number + "' already exists");
                });
        String status = text(customerOrder.getStatus());
        BigDecimal total_amount = decimal(customerOrder.getTotalAmount());
        String customer_name = text(customerOrder.getCustomerName());
        String customer_email = text(customerOrder.getCustomerEmail());
        String phone_number = text(customerOrder.getPhoneNumber());
        String address = text(customerOrder.getAddress());
        String map_link = text(customerOrder.getMapLink());
        String payment_method = text(customerOrder.getPaymentMethod());

        existing.setOrderNumber(order_number);
        existing.setStatus(status);
        existing.setTotalAmount(total_amount);
        existing.setCustomerName(customer_name);
        existing.setCustomerEmail(customer_email);
        existing.setPhoneNumber(phone_number);
        existing.setAddress(address);
        existing.setMapLink(map_link);
        existing.setPaymentMethod(payment_method);

        return repository.save(existing);
    }

    private String text(String value){
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("require");
        }
        return value.trim();
    }

    private BigDecimal decimal(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO ) <= 0) {
            throw new IllegalArgumentException("Total amount is required");
        }

        return amount;
    }
}
