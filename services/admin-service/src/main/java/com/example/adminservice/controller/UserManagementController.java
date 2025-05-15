package com.example.adminservice.controller;

/**
 * Admin Service - User Management Controller
 *
 * This controller handles administrative user management functionality, including:
 * - Listing all users
 * - Creating new users
 * - Editing user details
 * - Deleting users
 * - Resetting user passwords
 *
 * Architecture Notes:
 * - The Admin Service provides administrative interfaces
 * - It communicates with the Data Access Service for user data operations
 * - It communicates with the Auth Service for current user information
 * - It requires admin privileges (user type=2) to access
 *
 * Key Responsibilities:
 * - Providing user management interfaces for administrators
 * - Enforcing admin-only access to user management
 * - Handling user CRUD operations via the Data Access Service
 *
 * In our microservices architecture:
 * - This service is protected by Nginx's auth_request directive
 * - The Auth Service verifies admin privileges
 * - The Data Access Service handles the actual database operations
 * - All admin routes are under the /admin/ path prefix
 */

import com.example.adminservice.model.User;
import com.example.adminservice.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controller for user management functionality.
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */
@Controller
public class UserManagementController {

    /**
     * Service that handles user management business logic.
     *
     * This service:
     * - Communicates with the Data Access Service
     * - Handles user CRUD operations
     * - Implements business rules for user management
     *
     * @Autowired - Injects the UserManagementService bean into this controller
     */
    @Autowired
    private UserManagementService userManagementService;

    /**
     * WebClient for communicating with the Auth Service (via Nginx).
     *
     * This client is used to:
     * - Retrieve current user information
     * - Verify authentication status
     * - Get admin user details for the UI
     */
    private final WebClient authClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Nginx: As a gateway to the Auth Service
     */
    public UserManagementController() {
        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
    }

    /**
     * Redirects the root URL to the users management page.
     *
     * This is a convenience endpoint that redirects administrators from the
     * service root (/) to the main user management page (/admin/users).
     *
     * @GetMapping - Maps HTTP GET requests to "/"
     * @return A redirect instruction to "/admin/users"
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/admin/users";
    }

    /**
     * Displays the user management page with a list of all users.
     *
     * This endpoint:
     * - Retrieves all users from the Data Access Service
     * - Gets current admin user information for the UI
     * - Renders the users view with user listings
     *
     * Features:
     * - List of all users in the system
     * - User details including ID, name, email, and type
     * - Links to edit, delete, and reset password for each user
     *
     * Security:
     * - Protected by Nginx's auth_request directive
     * - Only accessible to admin users (type=2)
     * - Returns 403 Forbidden for non-admin users
     *
     * @GetMapping - Maps HTTP GET requests to "/users"
     * @param sessionId - The JSESSIONID cookie for authentication
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("users")
     */
    @GetMapping("/users")
    public String listUsers(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current admin user info from the auth service for the UI
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                // Request user information from Auth Service
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                // Add admin user info to model for UI personalization
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info if authentication fails
                // This should not happen in normal operation since Nginx enforces authentication
            }
        }

        // Retrieve all users from the Data Access Service via the UserManagementService
        List<User> users = userManagementService.getAllUsers();

        // Add users to model for display in the view
        model.addAttribute("users", users);

        // Return the users view template
        return "users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            Model model) {
        // Get the current user info from the auth service
        Map<String, Object> userInfo = null;
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        // Check if the user is trying to delete itself
        if (userInfo != null && userInfo.containsKey("id")) {
            Number idNumber = (Number) userInfo.get("id");
            if (idNumber != null) {
                Long currentUserId = idNumber.longValue();
                if (currentUserId.equals(userId)) {
                    model.addAttribute("errorMessage", "You cannot delete your own account");
                    List<User> users = userManagementService.getAllUsers();
                    model.addAttribute("users", users);
                    return "users";
                }
            }
        }

        boolean success = userManagementService.deleteUser(userId);

        if (success) {
            model.addAttribute("successMessage", "User deleted successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to delete user");
        }

        // Get the updated list of users and return the users page directly
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{userId}/reset-password")
    public String showResetPasswordForm(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        model.addAttribute("userId", userId);
        return "reset-password";
    }

    @PostMapping("/users/{userId}/reset-password")
    public String resetPassword(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            @RequestParam String newPassword,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        boolean success = userManagementService.resetPassword(userId, newPassword);

        if (success) {
            model.addAttribute("successMessage", "Password reset successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to reset password");
        }

        // Get the updated list of users and return the users page directly
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
}
