package com.example.authservice.service;

import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private DataAccessService dataAccessService;

    public User registerNewUser(User user) {
        // Check if user already exists
        if (dataAccessService.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // We'll let the data-access-service handle password encoding
        // to avoid double-hashing

        // Save user through data access service
        return dataAccessService.registerNewUser(user);
    }
}
