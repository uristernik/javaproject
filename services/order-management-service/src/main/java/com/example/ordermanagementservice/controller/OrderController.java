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
 * - It delegates business logic to the OrderService
 * - It delegates authentication logic to the AuthService
 * - It provides different views for customers and administrators
 *
 * Key Responsibilities:
 * - Handling HTTP requests and responses
 * - Managing view rendering and model attributes
 * - Delegating business logic to the service layer
 * - Implementing role-based access control for orders
 *
 * In our microservices architecture:
 * - This controller handles the presentation layer
 * - The OrderService handles business logic
 * - The AuthService handles authentication logic
 * - The Data Access Service handles the actual database operations
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.server.ResponseStatusException;

import com.example.ordermanagementservice.service.OrderService;
import com.example.ordermanagementservice.service.AuthService;

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
     * Service that handles order-related business logic.
     *
     * This service:
     * - Retrieves order data based on user role
     * - Implements role-based access control for orders
     *
     * @Autowired - Injects the OrderService bean into this controller
     */
    @Autowired
    private OrderService orderService;

    /**
     * Service that handles authentication-related business logic.
     *
     * This service:
     * - Retrieves user information from the Auth Service
     * - Validates authentication status
     * - Determines user roles and permissions
     *
     * @Autowired - Injects the AuthService bean into this controller
     */
    @Autowired
    private AuthService authService;

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

        // Get the current user information from the Auth Service
        Map<String, Object> userInfo = authService.getUserInfo(sessionId);

        // Extract user ID and determine if the user is an admin
        Long userId = authService.getUserId(userInfo);
        boolean isAdmin = authService.isAdmin(userInfo);

        // Retrieve orders based on user role (admin sees all, regular user sees own orders)
        List<Map<String, Object>> orders = orderService.getOrdersByUserRole(userId, isAdmin);

        // Add data to the model for rendering in the view
        model.addAttribute("orders", orders);     // Order data for display
        model.addAttribute("userInfo", userInfo); // User information for personalization

        // Return the orders view template
        return "orders";
    }
}
