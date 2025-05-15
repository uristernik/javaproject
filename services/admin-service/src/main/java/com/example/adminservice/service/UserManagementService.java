package com.example.adminservice.service;

/**
 * User Management Service - Service Layer
 *
 * This service handles user management business logic, including:
 * - Retrieving user data from the Data Access Service
 * - Processing user deletions
 * - Managing password resets
 * - Converting between data formats
 *
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles data transformation between API and domain models
 *
 * Key Responsibilities:
 * - Retrieving user information for the admin interface
 * - Processing user deletions
 * - Handling password resets
 * - Converting data between formats
 *
 * In our microservices architecture:
 * - This service focuses on user management business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 * - Only administrators can access this functionality
 */

import com.example.adminservice.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for user management functionality.
 *
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class UserManagementService {

    /**
     * WebClient for communicating with the Data Access Service.
     *
     * This client is used to:
     * - Retrieve user data from the database
     * - Delete users
     * - Reset user passwords
     */
    private final WebClient webClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public UserManagementService() {
        // Create WebClient for Data Access Service
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    /**
     * Retrieves all users from the database.
     *
     * This method:
     * - Fetches user data from the Data Access Service
     * - Transforms the raw data into User domain objects
     * - Returns a list of users for display in the admin interface
     *
     * @return List of User objects representing all users in the system
     */
    public List<User> getAllUsers() {
        // Retrieve user data from Data Access Service
        List<Map<String, Object>> userMaps = webClient.get()
                .uri("/api/data/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        // Handle null response
        if (userMaps == null) {
            return List.of();
        }

        // Convert raw data to domain objects
        return userMaps.stream()
                .map(this::convertToUser)
                .toList();
    }

    /**
     * Converts a raw user data map to a User domain object.
     *
     * This method:
     * - Safely extracts values from the raw data map
     * - Handles missing fields gracefully
     * - Converts data types as needed
     * - Populates a User object with the extracted data
     *
     * @param userMap The raw user data map from the database
     * @return A User object populated with the data
     */
    private User convertToUser(Map<String, Object> userMap) {
        User user = new User();
        // Extract and set user ID if present
        if (userMap.containsKey("id")) {
            user.setId(((Number) userMap.get("id")).longValue());
        }
        // Extract and set first name if present
        if (userMap.containsKey("firstName")) {
            user.setFirstName((String) userMap.get("firstName"));
        }
        // Extract and set last name if present
        if (userMap.containsKey("lastName")) {
            user.setLastName((String) userMap.get("lastName"));
        }
        // Extract and set email if present
        if (userMap.containsKey("email")) {
            user.setEmail((String) userMap.get("email"));
        }
        // Extract and set phone if present
        if (userMap.containsKey("phone")) {
            user.setPhone((String) userMap.get("phone"));
        }
        // Extract and set user type if present
        if (userMap.containsKey("type")) {
            user.setType(((Number) userMap.get("type")).intValue());
        }
        return user;
    }

    /**
     * Deletes a user from the system.
     *
     * This method:
     * - Sends a delete request to the Data Access Service
     * - Passes the user ID to identify the user to delete
     * - Returns the result of the operation
     *
     * @param userId The ID of the user to delete
     * @return true if the user was successfully deleted, false otherwise
     */
    public boolean deleteUser(Long userId) {
        // Send delete request to Data Access Service
        return webClient.delete()
                .uri("/api/data/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    /**
     * Resets a user's password.
     *
     * This method:
     * - Creates a data map with the user ID and new password
     * - Sends a password reset request to the Data Access Service
     * - Returns the result of the operation
     *
     * Security Note:
     * - The password will be hashed by the Data Access Service before storage
     * - This method should only be accessible to administrators
     *
     * @param userId The ID of the user whose password to reset
     * @param newPassword The new password for the user
     * @return true if the password was successfully reset, false otherwise
     */
    public boolean resetPassword(Long userId, String newPassword) {
        // Create data map with user ID and new password
        Map<String, Object> passwordData = new HashMap<>();
        passwordData.put("userId", userId);
        passwordData.put("newPassword", newPassword);

        // Send password reset request to Data Access Service
        return webClient.post()
                .uri("/api/data/users/reset-password")
                .bodyValue(passwordData)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
