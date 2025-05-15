package com.example.ordermanagementservice.controller;

/**
 * Order Management Service - Controller Layer
 *
 * This controller handles order management functionality, including:
 * - Viewing order history
 * - Order tracking
 * - Order status management
 *
 * Architecture Notes:
 * - The Order Management Service focuses on post-purchase order handling
 * - It communicates with the Data Access Service for order data
 * - It communicates with the Auth Service for user information
 * - It provides different views for customers and administrators
 *
 * Key Responsibilities:
 * - Displaying order history for customers
 * - Providing order management capabilities for administrators
 * - Showing order details and status
 * - Supporting order tracking
 *
 * In our microservices architecture:
 * - This service handles the post-purchase order lifecycle
 * - The Product Catalog Service handles the checkout process
 * - The Data Access Service handles the actual database operations
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Controller for order management functionality.
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */
@Controller
public class OrderController {
    /**
     * WebClient for communicating with the Data Access Service.
     *
     * This client is used to:
     * - Retrieve order data from the database
     * - Get order history for users
     * - Access order details and items
     */
    private final WebClient dataAccessClient;

    /**
     * WebClient for communicating with the Auth Service (via Nginx).
     *
     * This client is used to:
     * - Retrieve current user information
     * - Verify authentication status
     * - Check user roles for admin functionality
     */
    private final WebClient authClient;

    /**
     * Constructor that initializes the WebClient instances.
     *
     * The WebClients are configured to communicate with:
     * - Data Access Service: For database operations
     * - Nginx: As a gateway to the Auth Service
     */
    public OrderController() {
        // Create WebClient for Data Access Service
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");

        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
    }

    /**
     * Redirects the root URL to the orders page.
     *
     * This is a convenience endpoint that redirects users from the
     * service root (/) to the main orders page (/orders).
     *
     * @GetMapping - Maps HTTP GET requests to "/"
     * @return A redirect instruction to "/orders"
     */
    @GetMapping("/")
    public String redirectToOrders() {
        return "redirect:/orders";
    }

    /**
     * Displays the orders page with order history.
     *
     * This endpoint:
     * - Requires authentication (throws 401 if not authenticated)
     * - Shows different views for regular users and administrators
     * - Retrieves order data from the Data Access Service
     * - Renders the orders view with order listings
     *
     * Role-Based Access:
     * - Regular users (type=1): See only their own orders
     * - Admin users (type=2): See all orders in the system
     *
     * Security:
     * - Requires valid session cookie
     * - Verifies authentication with Auth Service
     * - Enforces role-based access control
     *
     * @GetMapping - Maps HTTP GET requests to "/orders"
     * @param sessionId - The JSESSIONID cookie for authentication
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("orders")
     * @throws ResponseStatusException with 401 Unauthorized if not authenticated
     */
    @GetMapping("/orders")
    public String showOrders(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {

        // Verify that the user is authenticated
        if (sessionId == null || sessionId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Get the current user information from the Auth Service
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

        // Extract user ID and type from user information
        Long userId = ((Number) userInfo.get("id")).longValue();
        Integer userType = ((Number) userInfo.get("type")).intValue();

        // Determine if the user is an admin (type=2)
        boolean isAdmin = userType == 2;

        // Implement role-based access control for orders
        // Admins see all orders, regular users see only their own orders
        List<Map<String, Object>> orders;
        if (isAdmin) {
            // Admin view: Retrieve all orders in the system
            orders = dataAccessClient.get()
                    .uri("/api/data/orders/all")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
        } else {
            // Regular user view: Retrieve only the user's own orders
            orders = dataAccessClient.get()
                    .uri("/api/data/orders/user/" + userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
        }

        // Add data to the model for rendering in the view
        model.addAttribute("orders", orders);     // Order data for display
        model.addAttribute("userInfo", userInfo); // User information for personalization

        // Return the orders view template
        return "orders";
    }
}
