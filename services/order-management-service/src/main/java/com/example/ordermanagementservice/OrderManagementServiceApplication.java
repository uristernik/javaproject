package com.example.ordermanagementservice;

/**
 * Order Management Service - Main Application
 *
 * This service handles order management functionality, including:
 * - Viewing order history
 * - Order tracking
 * - Order status management
 * - Order details display
 *
 * Architecture Notes:
 * - The Order Management Service runs on port 8084
 * - It follows a layered architecture with controllers and services
 * - It communicates with the Data Access Service for order data
 * - It communicates with the Auth Service for user information
 * - It provides different views for customers and administrators
 *
 * Key Components:
 * - OrderController: Handles order display and management
 * - OrderService: Implements order business logic
 *
 * In our microservices architecture:
 * - This service handles the post-purchase order lifecycle
 * - The Product Catalog Service handles the checkout process
 * - The Data Access Service handles the actual database operations
 * - Regular users see only their own orders
 * - Admin users can see all orders in the system
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Order Management Service.
 *
 * This class serves as the entry point for the Spring Boot application.
 * It initializes the Spring context and starts the embedded web server.
 *
 * @SpringBootApplication - A convenience annotation that adds:
 *   - @Configuration: Tags the class as a source of bean definitions
 *   - @EnableAutoConfiguration: Tells Spring Boot to configure beans based on classpath
 *   - @ComponentScan: Tells Spring to scan for components in the current package and subpackages
 */
@SpringBootApplication
public class OrderManagementServiceApplication {
    /**
     * Application entry point.
     *
     * This method:
     * 1. Sets the server port to 8084
     * 2. Starts the Spring Boot application
     * 3. Initializes the Spring application context
     * 4. Starts the embedded web server
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Set the server port for the Order Management Service
        System.setProperty("server.port", "8084");

        // Start the Spring Boot application
        SpringApplication.run(OrderManagementServiceApplication.class, args);
    }
}
