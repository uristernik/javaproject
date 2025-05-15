package com.example.ordermanagementservice.service;

/**
 * Order Service - Service Layer
 * 
 * This service handles order-related business logic, including:
 * - Retrieving order data from the Data Access Service
 * - Processing order information based on user roles
 * - Managing order status and tracking
 * 
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles data transformation between API and domain models
 * 
 * Key Responsibilities:
 * - Retrieving order information for display
 * - Implementing role-based access control for orders
 * - Processing order data for different user types
 * 
 * In our microservices architecture:
 * - This service focuses on order management business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 */

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Service for order management functionality.
 * 
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class OrderService {
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
     * Constructor that initializes the WebClient instance.
     * 
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public OrderService() {
        // Create WebClient for Data Access Service
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");
    }

    /**
     * Retrieves orders based on user role.
     * 
     * This method:
     * - Implements role-based access control for orders
     * - Retrieves all orders for admin users
     * - Retrieves only user's own orders for regular users
     * 
     * Business Rules:
     * - Admin users (type=2) can see all orders in the system
     * - Regular users (type=1) can only see their own orders
     * 
     * @param userId The ID of the current user
     * @param isAdmin Whether the current user is an admin
     * @return List of orders based on user role
     */
    public List<Map<String, Object>> getOrdersByUserRole(Long userId, boolean isAdmin) {
        if (isAdmin) {
            // Admin view: Retrieve all orders in the system
            return getAllOrders();
        } else {
            // Regular user view: Retrieve only the user's own orders
            return getUserOrders(userId);
        }
    }

    /**
     * Retrieves all orders in the system.
     * 
     * This method:
     * - Fetches all orders from the Data Access Service
     * - Used for admin users who can see all orders
     * 
     * @return List of all orders in the system
     */
    private List<Map<String, Object>> getAllOrders() {
        return dataAccessClient.get()
                .uri("/api/data/orders/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }

    /**
     * Retrieves orders for a specific user.
     * 
     * This method:
     * - Fetches orders for a specific user from the Data Access Service
     * - Used for regular users who can only see their own orders
     * 
     * @param userId The ID of the user whose orders to retrieve
     * @return List of orders for the specified user
     */
    private List<Map<String, Object>> getUserOrders(Long userId) {
        return dataAccessClient.get()
                .uri("/api/data/orders/user/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }
}
