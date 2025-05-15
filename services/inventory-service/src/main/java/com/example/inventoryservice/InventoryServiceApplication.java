package com.example.inventoryservice;

/**
 * Inventory Service - Main Application
 *
 * This service handles inventory management functionality, including:
 * - Tracking product stock levels
 * - Managing inventory additions from farmers
 * - Providing inventory information to other services
 *
 * Architecture Notes:
 * - The Inventory Service runs on port 8081
 * - It communicates with the Data Access Service for database operations
 * - It communicates with the Auth Service for user information
 * - It provides interfaces for inventory management
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Inventory Service.
 *
 * @SpringBootApplication - Enables auto-configuration and component scanning
 */
@SpringBootApplication
public class InventoryServiceApplication {
    /**
     * Application entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set the server port for the Inventory Service
        System.setProperty("server.port", "8081");
        // Start the Spring Boot application
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
