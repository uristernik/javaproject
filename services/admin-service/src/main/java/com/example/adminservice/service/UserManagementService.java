package com.example.adminservice.service;

/**
 * User Management Service - Service Layer
 *
 * This service handles user management business logic, including:
 * - Retrieving user data from the Data Access Service
 * - Processing user deletions
 * - Managing password resets
 * - Converting between data formats
 * - Retrieving user authentication information
 *
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It communicates with the Auth Service for user information
 * - It handles data transformation between API and domain models
 *
 * Key Responsibilities:
 * - Retrieving user information for the admin interface
 * - Processing user deletions
 * - Handling password resets
 * - Converting data between formats
 * - Retrieving authenticated user information
 * - Validating user operations (e.g., preventing self-deletion)
 *
 * In our microservices architecture:
 * - This service focuses on user management business logic
 * - The Data Access Service handles the actual database operations
 * - The Auth Service handles user authentication
 * - The controller handles HTTP concerns and view rendering
 * - Only administrators can access this functionality
 */

import com.example.adminservice.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient dataAccessClient;

    /**
     * WebClient for communicating with the Auth Service (via Nginx).
     *
     * This client is used to:
     * - Retrieve current admin user information
     * - Verify authentication status
     * - Get admin user details for the UI
     */
    private final WebClient authClient;

    /**
     * Constructor that initializes the WebClient instances.
     *
     * The WebClients are configured to communicate with:
     * - Data Access Service: For database operations
     * - Auth Service (via Nginx): For user authentication
     */
    public UserManagementService() {
        // Create WebClient for Data Access Service
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");
        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
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
        List<Map<String, Object>> userMaps = dataAccessClient.get()
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
     * Retrieves current user information from the Auth Service.
     *
     * This method:
     * - Makes a request to the Auth Service with the session ID
     * - Retrieves user details including ID, name, email, and type
     * - Returns a map of user information for UI personalization
     *
     * @param sessionId The JSESSIONID cookie for authentication
     * @return Map containing user information, or null if authentication fails
     */
    public Map<String, Object> getCurrentUserInfo(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }

        try {
            // Request user information from Auth Service
            return authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            // Return null if authentication fails
            return null;
        }
    }

    /**
     * Checks if the current user is attempting to delete their own account.
     *
     * This method:
     * - Extracts the current user's ID from the user info map
     * - Compares it with the target user ID for deletion
     * - Returns true if they match (self-deletion attempt)
     *
     * @param userInfo The current user's information map
     * @param targetUserId The ID of the user to be deleted
     * @return true if it's a self-deletion attempt, false otherwise
     */
    public boolean isAttemptingToDeleteSelf(Map<String, Object> userInfo, Long targetUserId) {
        if (userInfo == null || !userInfo.containsKey("id") || targetUserId == null) {
            return false;
        }

        Number idNumber = (Number) userInfo.get("id");
        if (idNumber == null) {
            return false;
        }

        Long currentUserId = idNumber.longValue();
        return currentUserId.equals(targetUserId);
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
        return dataAccessClient.delete()
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
        return dataAccessClient.post()
                .uri("/api/data/users/reset-password")
                .bodyValue(passwordData)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
