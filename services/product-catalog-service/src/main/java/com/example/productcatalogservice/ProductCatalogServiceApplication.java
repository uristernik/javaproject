package com.example.productcatalogservice;

/**
 * Product Catalog Service - Main Application
 *
 * This service handles product catalog functionality, including:
 * - Displaying product listings with images and details
 * - Managing the shopping basket functionality
 * - Processing checkout and order creation
 * - Providing a personalized user experience
 *
 * Architecture Notes:
 * - The Product Catalog Service runs on port 8082
 * - It follows a layered architecture with controllers and services
 * - It communicates with the Data Access Service for database operations
 * - It communicates with the Auth Service for user information
 * - It provides customer-facing interfaces for browsing and purchasing
 *
 * Key Components:
 * - ProductCatalogController: Handles HTTP requests and view rendering
 * - ProductService: Encapsulates business logic and data access
 *
 * In our microservices architecture:
 * - This service focuses on product presentation and ordering
 * - The Data Access Service handles database operations
 * - The Auth Service handles authentication and user information
 * - The Order Management Service handles post-purchase order processing
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Product Catalog Service.
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
public class ProductCatalogServiceApplication {
    /**
     * Application entry point.
     *
     * This method:
     * 1. Sets the server port to 8082
     * 2. Starts the Spring Boot application
     * 3. Initializes the Spring application context
     * 4. Starts the embedded web server
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Set the server port for the Product Catalog Service
        System.setProperty("server.port", "8082");

        // Start the Spring Boot application
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }
}
