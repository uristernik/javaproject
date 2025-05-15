package com.example.authservice.service;

/**
 * Data Access Service - Service Layer
 *
 * This service handles communication with the Data Access Service microservice, including:
 * - User lookup by email
 * - User existence checking
 * - User registration
 * - Data transformation between API and domain models
 *
 * Architecture Notes:
 * - This service encapsulates all communication with the Data Access Service
 * - It handles data transformation between services
 * - It provides error handling for network and data issues
 * - It includes detailed logging for troubleshooting
 *
 * Key Responsibilities:
 * - Finding users by email for authentication
 * - Checking if emails are already registered
 * - Registering new users in the database
 * - Converting between data formats
 *
 * In our microservices architecture:
 * - This service is the bridge to the Data Access Service
 * - The Data Access Service handles the actual database operations
 * - This service handles data transformation and error handling
 */

import com.example.authservice.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for communicating with the Data Access Service microservice.
 *
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class DataAccessService {
    /**
     * WebClient for communicating with the Data Access Service.
     *
     * This client is used to:
     * - Find users by email
     * - Check if emails exist
     * - Register new users
     */
    private final WebClient webClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public DataAccessService() {
        // Create WebClient for Data Access Service
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    /**
     * Finds a user by their email address.
     *
     * This method:
     * 1. Queries the Data Access Service for a user with the given email
     * 2. Transforms the returned data into a User domain object
     * 3. Returns the user wrapped in an Optional, or empty if not found
     *
     * Used For:
     * - Authentication during login
     * - Retrieving user details for authorization checks
     * - Getting user information for the current session
     *
     * Error Handling:
     * - Returns Optional.empty() if the user is not found
     * - Returns Optional.empty() if an exception occurs during the request
     * - Logs detailed error information for troubleshooting
     *
     * @param email The email address to search for
     * @return Optional containing the User if found, or empty if not found
     */
    public Optional<User> findUserByEmail(String email) {
        System.out.println("DataAccessService: Finding user by email: " + email);
        try {
            // Log the request for troubleshooting
            System.out.println("Making request to: http://data-access-service:8085/api/data/users/email/" + email);

            // Query the Data Access Service for the user
            Map<String, Object> userData = webClient.get()
                    .uri("/api/data/users/email/{email}", email)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // Handle null response (user not found)
            if (userData == null) {
                System.out.println("DataAccessService: No user data returned for email: " + email);
                return Optional.empty();
            }

            // Log the received data for troubleshooting
            System.out.println("DataAccessService: User data received: " + userData);

            // Transform the raw data into a User domain object
            User user = new User();
            user.setId(((Number) userData.get("id")).longValue());
            user.setFirstName((String) userData.get("firstName"));
            user.setLastName((String) userData.get("lastName"));
            user.setEmail((String) userData.get("email"));
            user.setPhone((String) userData.get("phone"));

            // The password field might be coming with a different name from the database
            // Try both "password" and "hashedPassword" keys
            String password = (String) userData.get("password");
            if (password == null || password.isEmpty()) {
                System.out.println("Password not found under 'password' key, trying 'hashedPassword'");
                password = (String) userData.get("hashedPassword");
            }

            user.setPassword(password);
            user.setType(((Number) userData.get("type")).intValue());

            // Log the created user object for troubleshooting
            System.out.println("DataAccessService: User object created with email: " + user.getEmail());
            System.out.println("DataAccessService: Password from DB: " + user.getPassword());

            return Optional.of(user);
        } catch (Exception e) {
            // Log the exception for troubleshooting
            System.err.println("DataAccessService: Error finding user by email: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Checks if a user with the given email already exists.
     *
     * This method:
     * 1. Queries the Data Access Service to check if the email exists
     * 2. Returns true if the email is already registered, false otherwise
     *
     * Used For:
     * - Validating registration requests
     * - Preventing duplicate user accounts
     *
     * Error Handling:
     * - Returns false if an exception occurs during the request
     * - Logs error information for troubleshooting
     *
     * @param email The email address to check
     * @return true if a user with the email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        try {
            // Query the Data Access Service to check if the email exists
            Boolean exists = webClient.get()
                    .uri("/api/data/users/exists/{email}", email)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            // Return true if the email exists, false otherwise
            return exists != null && exists;
        } catch (Exception e) {
            // Log the exception for troubleshooting
            System.err.println("Error checking if email exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registers a new user in the system.
     *
     * This method:
     * 1. Transforms the User domain object into a data map
     * 2. Sends the registration request to the Data Access Service
     * 3. Transforms the response into a User domain object
     * 4. Returns the registered user with ID and other fields populated
     *
     * Registration Process:
     * - User data is sent to the Data Access Service
     * - Password is hashed by the Data Access Service
     * - User is assigned a unique ID
     * - User is created with regular user privileges (type=1)
     *
     * Error Handling:
     * - Throws RuntimeException if registration fails
     * - Logs detailed error information for troubleshooting
     *
     * @param user The User object containing registration information
     * @return The registered User with ID and other fields populated
     * @throws RuntimeException if registration fails
     */
    public User registerNewUser(User user) {
        // Log the registration request for troubleshooting
        System.out.println("DataAccessService: Registering new user with email: " + user.getEmail());

        // Transform the User domain object into a data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("password", user.getPassword());

        // Log the registration data for troubleshooting
        System.out.println("DataAccessService: Sending registration data to data-access-service");

        try {
            // Send the registration request to the Data Access Service
            Map<String, Object> newUserData = webClient.post()
                    .uri("/api/data/users/register")
                    .bodyValue(userData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // Handle null response (registration failed)
            if (newUserData == null) {
                System.err.println("DataAccessService: Received null response from data-access-service");
                throw new RuntimeException("Failed to register user");
            }

            // Log the registration response for troubleshooting
            System.out.println("DataAccessService: Registration successful, received user data: " + newUserData);

            // Transform the response into a User domain object
            User newUser = new User();
            newUser.setId(((Number) newUserData.get("id")).longValue());
            newUser.setFirstName((String) newUserData.get("firstName"));
            newUser.setLastName((String) newUserData.get("lastName"));
            newUser.setEmail((String) newUserData.get("email"));
            newUser.setPhone((String) newUserData.get("phone"));
            newUser.setType(((Number) newUserData.get("type")).intValue());

            // Log the created user object for troubleshooting
            System.out.println("DataAccessService: Created new user object with ID: " + newUser.getId());
            return newUser;
        } catch (Exception e) {
            // Log the exception for troubleshooting
            System.err.println("DataAccessService: Error registering new user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }
}
