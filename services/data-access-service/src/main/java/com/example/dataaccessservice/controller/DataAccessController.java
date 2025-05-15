package com.example.dataaccessservice.controller;

/**
 * Data Access Service - Controller Layer
 *
 * This controller is the central access point for all database operations in the microservices architecture.
 * It provides a REST API that other services use to interact with the database.
 *
 * Architecture Notes:
 * - The Data Access Service centralizes all database operations
 * - It acts as an abstraction layer between other services and the database
 * - It handles CRUD operations for all entities (users, orders, inventory, etc.)
 * - It implements data validation and business logic related to data integrity
 * - It manages transactions to ensure data consistency
 *
 * Key Design Patterns:
 * - Repository Pattern: Abstracts data access logic
 * - Facade Pattern: Provides a simplified interface to the database
 * - DTO Pattern: Uses Map<String, Object> as flexible data transfer objects
 *
 * Architectural Benefits:
 * - Separation of Concerns: Other services focus on business logic, not data access
 * - Consistent Data Access: Standardized approach to database operations
 * - Centralized Security: Database credentials only needed in one service
 * - Schema Isolation: Database schema changes only affect one service
 */

import com.example.dataaccessservice.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes database operations as HTTP endpoints.
 *
 * @RestController - Combines @Controller and @ResponseBody, indicating that this class handles
 *                   HTTP requests and the return values should be bound to the web response body.
 *
 * @RequestMapping - Maps all endpoints in this controller to the "/api/data" base path.
 */
@RestController
@RequestMapping("/api/data")
public class DataAccessController {

    /**
     * The DatabaseService handles all database operations.
     *
     * In our microservices architecture:
     * - This service encapsulates all SQL queries and database logic
     * - It manages database connections and transactions
     * - It implements business rules related to data integrity
     *
     * @Autowired - Injects the DatabaseService bean into this controller
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Retrieves a list of all tables in the database.
     *
     * This endpoint is primarily used for:
     * - Administrative purposes
     * - Debugging and monitoring
     * - Schema exploration
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/tables"
     * @return ResponseEntity containing a list of table names
     */
    @GetMapping("/tables")
    public ResponseEntity<List<String>> getAllTables() {
        return ResponseEntity.ok(databaseService.getAllTables());
    }

    /**
     * Retrieves all data from a specific table.
     *
     * This endpoint provides:
     * - Generic data access for any table
     * - Flexible querying capabilities for other services
     * - Raw data access for administrative interfaces
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/tables/{tableName}"
     * @PathVariable - Extracts the table name from the URL path
     * @param tableName - The name of the table to query
     * @return ResponseEntity containing a list of records as maps
     */
    @GetMapping("/tables/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return ResponseEntity.ok(databaseService.getTableData(tableName));
    }

    /**
     * Updates data in a specific table.
     *
     * This endpoint:
     * - Provides a generic update mechanism for any table
     * - Accepts a map of column names and values to update
     * - Delegates to DatabaseService for transaction management
     *
     * @PostMapping - Maps HTTP POST requests to "/api/data/tables/{tableName}/update"
     * @PathVariable - Extracts the table name from the URL path
     * @RequestBody - Binds the HTTP request body to the updateData parameter
     * @param tableName - The name of the table to update
     * @param updateData - Map containing the data to update
     * @return ResponseEntity with no content but success status
     */
    @PostMapping("/tables/{tableName}/update")
    public ResponseEntity<Void> updateTableData(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> updateData) {
        databaseService.updateTableData(tableName, updateData);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new order in the database.
     *
     * This endpoint:
     * - Processes order creation requests from the Order Service
     * - Creates the order record in the database
     * - Handles related order items in a transaction
     * - Returns the generated order ID
     *
     * In our microservices architecture:
     * - The Order Service handles order business logic
     * - This service handles the database persistence
     *
     * @PostMapping - Maps HTTP POST requests to "/api/data/orders/create"
     * @RequestBody - Binds the HTTP request body to the orderData parameter
     * @param orderData - Map containing order information (user ID, items, etc.)
     * @return ResponseEntity containing the newly created order ID
     */
    @PostMapping("/orders/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        Long orderId = databaseService.createOrder(orderData);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    /**
     * Updates multiple inventory items in a single batch operation.
     *
     * This endpoint:
     * - Processes batch inventory updates from the Inventory Service
     * - Updates multiple product quantities in a single transaction
     * - Ensures data consistency across related updates
     *
     * Use cases:
     * - Order fulfillment (reducing stock levels)
     * - Inventory restocking
     * - Price updates
     *
     * @PostMapping - Maps HTTP POST requests to "/api/data/inventory/update-batch"
     * @RequestBody - Binds the HTTP request body to the updates parameter
     * @param updates - List of maps containing inventory updates
     * @return ResponseEntity with no content but success status
     */
    @PostMapping("/inventory/update-batch")
    public ResponseEntity<Void> updateInventoryBatch(@RequestBody List<Map<String, Object>> updates) {
        databaseService.updateInventoryBatch(updates);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * This endpoint:
     * - Provides order history for a specific user
     * - Returns order details including items and status
     * - Used by both customer-facing and admin interfaces
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/orders/user/{userId}"
     * @PathVariable - Extracts the user ID from the URL path
     * @param userId - The ID of the user whose orders to retrieve
     * @return ResponseEntity containing a list of order records
     */
    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(databaseService.getUserOrders(userId));
    }

    /**
     * Retrieves all orders in the system.
     *
     * This endpoint:
     * - Provides a complete view of all orders
     * - Used primarily by administrative interfaces
     * - Supports order management and reporting
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/orders/all"
     * @return ResponseEntity containing a list of all order records
     */
    @GetMapping("/orders/all")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        return ResponseEntity.ok(databaseService.getAllOrders());
    }

    /**
     * User Management Endpoints
     *
     * These endpoints handle all user-related database operations:
     * - User registration and authentication
     * - User profile management
     * - User administration (for admin users)
     *
     * In our microservices architecture:
     * - The Auth Service handles authentication logic
     * - The Admin Service handles user administration
     * - This service handles the actual database operations
     */

    /**
     * Retrieves all users in the system.
     *
     * This endpoint:
     * - Provides a complete list of all registered users
     * - Used primarily by administrative interfaces
     * - Supports user management and reporting
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/users"
     * @return ResponseEntity containing a list of all user records
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(databaseService.getAllUsers());
    }

    /**
     * Deletes a user from the system.
     *
     * This endpoint:
     * - Permanently removes a user from the database
     * - Used by administrative interfaces
     * - Returns success/failure status
     *
     * @DeleteMapping - Maps HTTP DELETE requests to "/api/data/users/{userId}"
     * @PathVariable - Extracts the user ID from the URL path
     * @param userId - The ID of the user to delete
     * @return ResponseEntity containing a boolean indicating success
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(databaseService.deleteUser(userId));
    }

    /**
     * Resets a user's password.
     *
     * This endpoint:
     * - Updates a user's password in the database
     * - Handles password hashing via the DatabaseService
     * - Used by both user self-service and admin interfaces
     *
     * Security considerations:
     * - Passwords are hashed using BCrypt before storage
     * - Only admins or the user themselves should be able to reset passwords
     *
     * @PostMapping - Maps HTTP POST requests to "/api/data/users/reset-password"
     * @RequestBody - Binds the HTTP request body to the passwordData parameter
     * @param passwordData - Map containing userId and newPassword
     * @return ResponseEntity containing a boolean indicating success
     */
    @PostMapping("/users/reset-password")
    public ResponseEntity<Boolean> resetUserPassword(@RequestBody Map<String, Object> passwordData) {
        Long userId = ((Number) passwordData.get("userId")).longValue();
        String newPassword = (String) passwordData.get("newPassword");
        return ResponseEntity.ok(databaseService.resetUserPassword(userId, newPassword));
    }

    /**
     * Retrieves a user by their email address.
     *
     * This endpoint:
     * - Looks up a user by their unique email address
     * - Used primarily for authentication
     * - Returns 404 if the user doesn't exist
     *
     * In our microservices architecture:
     * - The Auth Service calls this endpoint during login
     * - The CustomUserDetailsService uses this to load user details
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/users/email/{email}"
     * @PathVariable - Extracts the email from the URL path
     * @param email - The email address to look up
     * @return ResponseEntity containing the user record or 404 if not found
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Map<String, Object> user = databaseService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Checks if an email address is already registered.
     *
     * This endpoint:
     * - Verifies if an email is already in use
     * - Used during user registration to prevent duplicates
     * - Returns a boolean indicating existence
     *
     * @GetMapping - Maps HTTP GET requests to "/api/data/users/exists/{email}"
     * @PathVariable - Extracts the email from the URL path
     * @param email - The email address to check
     * @return ResponseEntity containing a boolean (true if email exists)
     */
    @GetMapping("/users/exists/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        return ResponseEntity.ok(databaseService.existsByEmail(email));
    }

    /**
     * Registers a new user in the system.
     *
     * This endpoint:
     * - Creates a new user record in the database
     * - Handles password hashing via the DatabaseService
     * - Performs validation and returns appropriate errors
     *
     * Security considerations:
     * - Passwords are hashed using BCrypt before storage
     * - Email uniqueness is enforced
     *
     * In our microservices architecture:
     * - The Auth Service calls this during user registration
     * - This service handles the actual database operation
     *
     * @PostMapping - Maps HTTP POST requests to "/api/data/users/register"
     * @RequestBody - Binds the HTTP request body to the userData parameter
     * @param userData - Map containing user registration data
     * @return ResponseEntity containing the new user or error message
     */
    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> newUser = databaseService.registerNewUser(userData);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
