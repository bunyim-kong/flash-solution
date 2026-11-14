package com.example.back_end.service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // function create
    @Override
    @Transactional
    public User createUser(User user) {
        user.setId(null);
        user.setFullName(text(user.getFullName(), "User name"));
        user.setEmail(text(user.getEmail(), "User email").toLowerCase(Locale.ROOT));
        user.setPasswordHash(text(user.getPasswordHash(), "User password"));
        user.setRole(text(user.getRole(), "User role"));

        checkUnique(user.getEmail(), null);
        return userRepository.save(user);
    }

    // function read
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByNameAsc();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("User with Id " + id + " was not found."));
    }

    // function update
    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existing = getUserById(id);
        String email = text(user.getEmail(), "User email").toLowerCase(Locale.ROOT);

        checkUnique(email, existing.getEmail());

        existing.setFullName(text(user.getFullName(), "User name"));
        existing.setEmail(email);
        existing.setPasswordHash(text(user.getPasswordHash(), "User password"));
        existing.setRole(text(user.getRole(), "User role"));
        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
    }

    private void checkUnique(String email, String oldEmail) {
        if (!email.equals(oldEmail) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A User with " + email + " already exist.");
        }
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value.trim();
    }
}
