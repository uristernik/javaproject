package com.example.adminservice;

/**
 * Admin Service - Main Application
 *
 * This service handles administrative functionality, including:
 * - User management (viewing, editing, deleting users)
 * - Price management for products
 * - Order management and oversight
 * - System configuration and monitoring
 *
 * Architecture Notes:
 * - The Admin Service runs on port 8080 (default)
 * - It follows a layered architecture with controllers and services
 * - It communicates with the Data Access Service for database operations
 * - It communicates with the Auth Service for user information
 * - It requires admin privileges (user type=2) to access
 *
 * Key Components:
 * - UserManagementController: Handles user administration
 * - PriceManagementController: Manages product pricing
 * - UserManagementService: Implements user management business logic
 * - PriceManagementService: Implements price management business logic
 *
 * In our microservices architecture:
 * - This service is protected by Nginx's auth_request directive
 * - The Auth Service verifies admin privileges
 * - The Data Access Service handles the actual database operations
 * - All admin routes are under the /admin/ path prefix
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Admin Service.
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
public class AdminServiceApplication {
    /**
     * Application entry point.
     *
     * This method:
     * 1. Starts the Spring Boot application
     * 2. Initializes the Spring application context
     * 3. Starts the embedded web server on the default port (8080)
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
