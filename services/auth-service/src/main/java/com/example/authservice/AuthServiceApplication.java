package com.example.authservice;

/**
 * Auth Service - Main Application
 *
 * This service handles authentication and authorization functionality, including:
 * - User login and session management
 * - User registration
 * - Authentication verification for other services
 * - User information retrieval
 *
 * Architecture Notes:
 * - The Auth Service runs on port 8086
 * - It follows a layered architecture with controllers and services
 * - It communicates with the Data Access Service for user data
 * - It provides authentication verification for Nginx's auth_request directive
 * - It manages user sessions using Spring Security
 *
 * Key Components:
 * - SecurityConfig: Configures Spring Security for authentication
 * - AuthController: Handles login and registration
 * - AuthCheckController: Verifies authentication status
 * - CustomUserDetailsService: Loads user details for authentication
 *
 * In our microservices architecture:
 * - This service is the central authority for authentication
 * - Nginx uses this service to protect routes
 * - Other services retrieve user information from this service
 * - The Data Access Service handles the actual user data storage
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Auth Service.
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
public class AuthServiceApplication {
    /**
     * Application entry point.
     *
     * This method:
     * 1. Sets the server port to 8086
     * 2. Starts the Spring Boot application
     * 3. Initializes the Spring application context
     * 4. Starts the embedded web server
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Set the server port for the Auth Service
        System.setProperty("server.port", "8086");

        // Start the Spring Boot application
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
