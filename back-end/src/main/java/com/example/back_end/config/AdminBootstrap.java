package com.example.back_end.config;

import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;
import com.example.back_end.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final UserService userService;
    private final String email;
    private final String password;

    public AdminBootstrap(UserRepository users, UserService userService,
                          @Value("${LUMIERE_ADMIN_EMAIL:}") String email,
                          @Value("${LUMIERE_ADMIN_PASSWORD:}") String password) {
        this.users = users;
        this.userService = userService;
        this.email = email.trim();
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isEmpty() || password.isEmpty() || users.existsByRoleIgnoreCase("ADMIN")) return;
        User admin = new User();
        admin.setName("Store Administrator");
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setRole("ADMIN");
        userService.createUser(admin);
    }
}
