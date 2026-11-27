package com.example.back_end.controller;

import com.example.back_end.config.ApiAccess;
import com.example.back_end.model.User;
import com.example.back_end.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    public record Quantity(int quantity) {}
    public record Saved(boolean saved) {}

    @GetMapping
    public CartService.View get(HttpServletRequest request) {
        return service.get(userId(request), request.getSession(false));
    }

    @PostMapping("/items/{productId}")
    public CartService.View add(@PathVariable Long productId, HttpServletRequest request) {
        return service.setQuantity(userId(request), request.getSession(), productId, 1, true);
    }

    @PutMapping("/items/{productId}")
    public CartService.View update(@PathVariable Long productId, @RequestBody Quantity body, HttpServletRequest request) {
        return service.setQuantity(userId(request), request.getSession(), productId, body.quantity(), false);
    }

    @DeleteMapping("/items/{productId}")
    public CartService.View remove(@PathVariable Long productId, HttpServletRequest request) {
        return service.setQuantity(userId(request), request.getSession(), productId, 0, false);
    }

    @PutMapping("/saved/{productId}")
    public CartService.View save(@PathVariable Long productId, @RequestBody Saved body, HttpServletRequest request) {
        return service.saveProduct(userId(request), request.getSession(), productId, body.saved());
    }

    private Long userId(HttpServletRequest request) {
        User user = ApiAccess.account(request);
        return user == null ? null : user.getId();
    }
}
