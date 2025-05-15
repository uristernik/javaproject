package com.example.ordermanagementservice.service;

/**
 * Auth Service - Service Layer
 * 
 * This service handles authentication-related business logic, including:
 * - Retrieving user information from the Auth Service
 * - Validating authentication status
 * - Extracting user roles and permissions
 * 
 * Architecture Notes:
 * - This service encapsulates authentication logic separate from the controller
 * - It communicates with the Auth Service for user information
 * - It handles authentication validation and error handling
 * 
 * Key Responsibilities:
 * - Retrieving user information for the current session
 * - Validating authentication status
 * - Determining user roles and permissions
 * 
 * In our microservices architecture:
 * - This service focuses on authentication-related business logic
 * - The Auth Service handles the actual authentication
 * - The controller handles HTTP concerns and view rendering
 */

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Service for authentication-related functionality.
 * 
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class AuthService {
    /**
     * WebClient for communicating with the Auth Service.
     * 
     * This client is used to:
     * - Retrieve user information
     * - Validate authentication status
     * - Determine user roles and permissions
     */
    private final WebClient authClient;

    /**
     * Constructor that initializes the WebClient instance.
     * 
     * The WebClient is configured to communicate with:
     * - Nginx: As a gateway to the Auth Service
     */
    public AuthService() {
        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
    }

    /**
     * Retrieves user information for the current session.
     * 
     * This method:
     * - Validates that the session ID is not empty
     * - Retrieves user information from the Auth Service
     * - Validates that the user information is valid
     * - Throws appropriate exceptions if authentication fails
     * 
     * @param sessionId The JSESSIONID cookie for authentication
     * @return Map containing user information (id, email, firstName, lastName, type)
     * @throws ResponseStatusException with 401 Unauthorized if authentication fails
     */
    public Map<String, Object> getUserInfo(String sessionId) {
        // Verify that the session ID is not empty
        if (sessionId == null || sessionId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Retrieve user information from the Auth Service
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            // Authentication failed - throw 401 Unauthorized
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated: " + e.getMessage());
        }

        // Verify that user information is valid
        if (userInfo == null || !userInfo.containsKey("id")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return userInfo;
    }

    /**
     * Determines if the user is an admin based on user type.
     * 
     * This method:
     * - Extracts the user type from user information
     * - Returns true if the user is an admin (type=2), false otherwise
     * 
     * @param userInfo Map containing user information
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(Map<String, Object> userInfo) {
        Integer userType = ((Number) userInfo.get("type")).intValue();
        return userType == 2;
    }

    /**
     * Extracts the user ID from user information.
     * 
     * This method:
     * - Extracts the user ID from user information
     * - Converts it to a Long
     * 
     * @param userInfo Map containing user information
     * @return The user ID as a Long
     */
    public Long getUserId(Map<String, Object> userInfo) {
        return ((Number) userInfo.get("id")).longValue();
    }
}
