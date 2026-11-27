package com.example.back_end.controller;

import com.example.back_end.config.ApiAccess;
import com.example.back_end.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CheckoutController {
    private final CheckoutService checkout;

    public CheckoutController(CheckoutService checkout) {
        this.checkout = checkout;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutService.Order create(@RequestBody CheckoutService.Request body, HttpServletRequest request) {
        return checkout.place(ApiAccess.requireAccount(request), body);
    }

    @GetMapping("/orders/my")
    public List<CheckoutService.Order> myOrders(HttpServletRequest request) {
        return checkout.history(ApiAccess.requireAccount(request).getId());
    }

    @GetMapping("/admin/orders")
    public List<CheckoutService.Order> allOrders() {
        return checkout.allOrders();
    }
}
