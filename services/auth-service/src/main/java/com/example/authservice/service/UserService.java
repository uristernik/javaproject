package com.example.authservice.service;

/**
 * User Service - Service Layer
 *
 * This service handles user-related business logic, including:
 * - User registration
 * - User data validation
 * - Delegating data operations to the Data Access Service
 *
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles validation before data operations
 *
 * Key Responsibilities:
 * - Validating user registration data
 * - Checking for duplicate email addresses
 * - Delegating user creation to the Data Access Service
 *
 * In our microservices architecture:
 * - This service focuses on user management business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 */

import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for user management functionality.
 *
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class UserService {

    /**
     * DataAccessService for communicating with the Data Access Service.
     *
     * This service is used to:
     * - Check if a user already exists
     * - Register new users in the database
     * - Retrieve user information
     *
     * @Autowired - Injects the DataAccessService bean into this service
     */
    @Autowired
    private DataAccessService dataAccessService;

    /**
     * Registers a new user in the system.
     *
     * This method:
     * 1. Validates that the email is not already in use
     * 2. Delegates user creation to the Data Access Service
     * 3. Returns the created user with ID assigned
     *
     * Business Rules:
     * - Email addresses must be unique
     * - Password hashing is handled by the Data Access Service
     * - New users are created with regular user privileges (type=1)
     *
     * @param user The User object containing registration information
     * @return The registered User with ID assigned
     * @throws RuntimeException if the email is already in use
     */
    public User registerNewUser(User user) {
        // Check if user already exists by email
        if (dataAccessService.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // We'll let the data-access-service handle password encoding
        // to avoid double-hashing

        // Save user through data access service
        return dataAccessService.registerNewUser(user);
    }
}
