package com.example.back_end.controller;

import com.example.back_end.config.ApiAccess;
import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;
import com.example.back_end.service.CartService;
import com.example.back_end.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final CartService carts;
    private final String dummyHash;

    public AuthController(UserRepository users, UserService userService, PasswordEncoder encoder, CartService carts) {
        this.users = users;
        this.userService = userService;
        this.encoder = encoder;
        this.carts = carts;
        this.dummyHash = encoder.encode(java.util.UUID.randomUUID().toString());
    }

    public record Credentials(String email, String password) {}
    public record Registration(String name, String email, String password) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody Registration registration, HttpServletRequest request) {
        User user = new User();
        user.setName(registration.name());
        user.setEmail(registration.email());
        user.setPassword(registration.password());
        user.setRole("CUSTOMER");
        User saved = userService.createUser(user);
        return signIn(saved, request);
    }

    @PostMapping("/login")
    public User login(@RequestBody Credentials credentials, HttpServletRequest request) {
        if (credentials.email() == null || credentials.password() == null
                || credentials.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw invalidCredentials();
        }
        User user = users.findByEmailIgnoreCase(credentials.email().trim()).orElse(null);
        String hash = user != null && user.getPassword() != null ? user.getPassword() : dummyHash;
        if (!encoder.matches(credentials.password(), hash) || user == null || !user.isActive()) {
            throw invalidCredentials();
        }
        return signIn(user, request);
    }

    @GetMapping("/me")
    public User me(HttpServletRequest request) {
        return ApiAccess.requireAccount(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
    }

    private User signIn(User user, HttpServletRequest request) {
        var session = request.getSession();
        synchronized (session) {
            carts.mergeGuest(user.getId(), session);
            session.removeAttribute(CartService.GUEST_SELECTION);
            request.changeSessionId();
            session.setAttribute(ApiAccess.ACCOUNT_ID, user.getId());
        }
        return user;
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect");
    }
}
