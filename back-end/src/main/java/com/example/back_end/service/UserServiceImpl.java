
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
        user.setName(text(user.getName(), "User name"));
        user.setEmail(text(user.getEmail(), "User email").toLowerCase(Locale.ROOT));
        user.setPassword(text(user.getPassword(), "User password"));
        user.setRole(text(user.getRole(), "User role"));

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
        User existing = getUserById(id);

        String name = text(user.getName(), "User name");
        String email = text(user.getEmail(), "User email").toLowerCase(Locale.ROOT);
        String password = text(user.getPassword(), "User password");
        String role = text(user.getRole(), "User role");

        checkUnique(name, email, existing.getName(), existing.getEmail());

        existing.setName(name);
        existing.setEmail(email);
        existing.setPassword(password);
        existing.setRole(role);

        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
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

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
