package com.example.back_end.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createHashesPasswordBeforeSaving() {
        User request = new User();
        request.setName("Customer");
        request.setEmail("CUSTOMER@EXAMPLE.COM");
        request.setPassword("plain-password");
        request.setRole("CUSTOMER");

        when(passwordEncoder.encode("plain-password")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.createUser(request);

        assertEquals("bcrypt-hash", saved.getPassword());
        assertEquals("customer@example.com", saved.getEmail());
        verify(passwordEncoder).encode("plain-password");
    }
}
