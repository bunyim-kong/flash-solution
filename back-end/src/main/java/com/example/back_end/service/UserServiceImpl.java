
package com.example.back_end.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.ShopSelectionRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final CustomerOrderRepository orders;
    private final ShopSelectionRepository selections;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           CustomerOrderRepository orders, ShopSelectionRepository selections) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orders = orders;
        this.selections = selections;
    }

    // function create
    @Override
    @Transactional
    public User createUser(User user) {
        user.setId(null);
        user.setName(text(user.getName(), "User name"));
        user.setEmail(email(user.getEmail()));
        user.setPassword(passwordEncoder.encode(password(user.getPassword())));
        user.setRole(role(user.getRole()));

        checkUnique(user.getName(), user.getEmail(), null, null);

        return userRepository.save(user);
    }

    // function read
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("User with ID " + id + " was not found"));
    }

    // function update
    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existing = lockUser(id);

        String name = text(user.getName(), "User name");
        String email = email(user.getEmail());
        String role = role(user.getRole());

        checkUnique(name, email, existing.getName(), existing.getEmail());

        existing.setName(name);
        existing.setEmail(email);
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(password(user.getPassword())));
        }
        existing.setRole(role);
        existing.setActive(user.isActive());

        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = lockUser(id);
        if (orders.existsByUserId(id)) {
            throw new IllegalArgumentException("This account has order history. Suspend it to remove access.");
        }
        selections.deleteById(id);
        userRepository.delete(user);
    }

    //

    private void checkUnique(String name, String email, String oldName, String oldEmail) {
        if (!name.equals(oldName) && userRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A User with " + name + " already exists");
        }

        if (!email.equals(oldEmail) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A User with " + email + " already exists");
        }
    }

    private User lockUser(Long id) {
        return userRepository.lockById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " was not found"));
    }

    private String email(String value) {
        String email = text(value, "User email").toLowerCase(Locale.ROOT);
        if (email.length() > 255 || !email.matches("[^\\s@]+@[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        return email;
    }

    private String role(String value) {
        String role = text(value, "User role").toUpperCase(Locale.ROOT);
        if (!role.equals("ADMIN") && !role.equals("CUSTOMER")) {
            throw new IllegalArgumentException("Choose a valid account role");
        }
        return role;
    }

    private String password(String value) {
        if (value == null || value.isBlank() || value.length() < 8
                || value.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Use at least 8 characters and at most 72 UTF-8 bytes for the password");
        }
        return value;
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
