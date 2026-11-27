package com.example.back_end.controller;

import java.util.List;
import com.example.back_end.config.ApiAccess;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.model.User;
import com.example.back_end.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    // READ (all)
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers ();
    }

    // READ (one)
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        if (id.equals(ApiAccess.requireAccount(request).getId())
                && (!user.isActive() || !ApiAccess.isAdmin(user))) {
            throw new IllegalArgumentException("Your own administrator account must stay active");
        }
        return userService.updateUser(id, user);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        if (id.equals(ApiAccess.requireAccount(request).getId())) {
            throw new IllegalArgumentException("You cannot remove your own account");
        }
        userService.deleteUser(id);
    }
}