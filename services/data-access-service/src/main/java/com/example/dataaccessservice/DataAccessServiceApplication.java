package com.example.dataaccessservice;

/**
 * Data Access Service - Main Application
 *
 * This service centralizes all database operations for the microservices architecture, including:
 * - User data management (registration, authentication, profiles)
 * - Inventory and product data operations
 * - Order processing and management
 * - General database table access
 *
 * Architecture Notes:
 * - The Data Access Service runs on port 8085
 * - It acts as an abstraction layer between other services and the database
 * - It provides a REST API for database operations
 * - It handles data validation and business rules related to data integrity
 * - It manages database connections and transactions
 *
 * Key Components:
 * - DataAccessController: Exposes REST endpoints for database operations
 * - DatabaseService: Implements database operations using JDBC
 * - Transaction management for data consistency
 *
 * In our microservices architecture:
 * - This service is the only one with direct database access
 * - Other services communicate with this service for data operations
 * - It centralizes database credentials and connection management
 * - It provides a consistent approach to data access across the system
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Data Access Service.
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
public class DataAccessServiceApplication {
    /**
     * Application entry point.
     *
     * This method:
     * 1. Sets the server port to 8085
     * 2. Starts the Spring Boot application
     * 3. Initializes the Spring application context
     * 4. Starts the embedded web server
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Set the server port for the Data Access Service
        System.setProperty("server.port", "8085");

        // Start the Spring Boot application
        SpringApplication.run(DataAccessServiceApplication.class, args);
    }
}
