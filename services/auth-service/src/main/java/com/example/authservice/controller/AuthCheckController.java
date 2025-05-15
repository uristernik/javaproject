package com.example.authservice.controller;

/**
 * Auth Service - Authentication Check Controller
 *
 * This controller is a critical component in the authentication architecture.
 * It provides endpoints for verifying authentication status and retrieving user information.
 *
 * Key responsibilities:
 * 1. Verify if a user is authenticated
 * 2. Check if a user has admin privileges for admin routes
 * 3. Provide current user information to other services
 *
 * Architecture Notes:
 * - This controller is called by Nginx's auth_request directive
 * - It works with Spring Security to check authentication status
 * - It communicates with the Data Access Service to retrieve user details
 * - It enforces role-based access control for admin routes
 *
 * Authentication Flow:
 * 1. User requests a protected resource
 * 2. Nginx makes an internal request to /auth/check
 * 3. This controller verifies authentication status
 * 4. For admin routes, it also checks admin privileges
 * 5. Returns appropriate status codes (200, 401, 403)
 * 6. Nginx handles the response accordingly
 */

import com.example.authservice.model.User;
import com.example.authservice.service.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

/**
 * REST controller that handles authentication verification and user information retrieval.
 *
 * @RestController - Combines @Controller and @ResponseBody, indicating that this class handles
 *                   HTTP requests and the return values should be bound to the web response body.
 *
 * @RequestMapping - Maps all endpoints in this controller to the "/auth" base path.
 */
@RestController
@RequestMapping("/auth")
public class AuthCheckController {

    /**
     * The DataAccessService provides access to user data in the database.
     *
     * In our microservices architecture:
     * - This service communicates with the Data Access Service
     * - It retrieves user information for authentication checks
     * - It's used to verify user roles and permissions
     *
     * @Autowired - Injects the DataAccessService bean into this controller
     */
    @Autowired
    private DataAccessService dataAccessService;

    /**
     * Verifies if a user is authenticated and has appropriate permissions.
     *
     * This endpoint is called by Nginx's auth_request directive to determine:
     * 1. If the user is authenticated
     * 2. For admin routes, if the user has admin privileges
     *
     * Response Status Codes:
     * - 200 OK: User is authenticated (and has admin privileges for admin routes)
     * - 401 Unauthorized: User is not authenticated
     * - 403 Forbidden: User is authenticated but lacks required privileges
     *
     * Authentication Flow:
     * 1. Extract the current authentication from Spring Security context
     * 2. Check if the user is authenticated and not anonymous
     * 3. For admin routes, verify the user has admin privileges (type=2)
     * 4. Return appropriate status code based on checks
     *
     * @GetMapping - Maps HTTP GET requests to "/auth/check"
     * @param request - The HTTP request containing headers from Nginx
     * @return ResponseEntity with appropriate status code and no content
     */
    @GetMapping("/check")
    public ResponseEntity<Void> checkAuth(HttpServletRequest request) {
        // Get the current authentication from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Get the original URI from the request headers set by Nginx
            String originalUri = request.getHeader("X-Original-URI");

            // Special handling for admin routes
            if (originalUri != null && originalUri.startsWith("/admin/")) {
                // Get user email from authentication
                String email = auth.getName();
                // Retrieve full user details from Data Access Service
                Optional<User> userOpt = dataAccessService.findUserByEmail(email);

                // Check if user exists and has admin privileges (type=2)
                if (userOpt.isPresent() && userOpt.get().getType() != null && userOpt.get().getType() == 2) {
                    return ResponseEntity.ok().build(); // 200 OK - User is admin
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden - Not admin
                }
            }

            // For non-admin routes, being authenticated is sufficient
            return ResponseEntity.ok().build(); // 200 OK - User is authenticated
        } else {
            // User is not authenticated
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
    }

    /**
     * Retrieves information about the currently authenticated user.
     *
     * This endpoint:
     * - Provides user profile information to other services
     * - Is used by UI components to display user information
     * - Returns user ID, email, name, and type (role)
     *
     * Use Cases:
     * - Displaying user information in the UI
     * - Personalizing content based on user details
     * - Determining available actions based on user type
     *
     * In our microservices architecture:
     * - The Product Catalog Service calls this to get user info for checkout
     * - The Admin Service uses it to display current user details
     * - Frontend components use it to personalize the UI
     *
     * @GetMapping - Maps HTTP GET requests to "/auth/user"
     * @return ResponseEntity containing user information or 401 if not authenticated
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        // Get the current authentication from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Get user email from authentication
            String email = auth.getName();
            // Retrieve full user details from Data Access Service
            Optional<User> userOpt = dataAccessService.findUserByEmail(email);

            if (userOpt.isPresent()) {
                // Extract user information
                User user = userOpt.get();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("firstName", user.getFirstName());
                userInfo.put("lastName", user.getLastName());
                userInfo.put("type", user.getType());

                // Return user information with 200 OK status
                return ResponseEntity.ok(userInfo);
            }
        }

        // User is not authenticated or not found
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
