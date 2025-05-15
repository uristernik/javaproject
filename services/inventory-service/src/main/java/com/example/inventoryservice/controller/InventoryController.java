package com.example.inventoryservice.controller;

/**
 * Inventory Service - Controller Layer
 *
 * This controller handles inventory management functionality, including:
 * - Displaying current inventory levels
 * - Managing inventory additions from farmers
 * - Providing inventory information to other services
 *
 * Key Responsibilities:
 * - Displaying inventory information
 * - Processing inventory additions from farmers
 * - Managing stock levels
 *
 * In our microservices architecture:
 * - This service focuses on inventory management
 * - The Data Access Service handles the actual database operations
 * - The Product Catalog Service uses inventory data for the catalog
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */

import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Controller
public class InventoryController {
    /**
     * Service that handles inventory business logic.
     *
     * This service:
     * - Communicates with the Data Access Service
     * - Retrieves inventory items
     * - Updates inventory quantities
     * - Implements business rules for inventory management
     *
     * @Autowired - Injects the InventoryService bean into this controller
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * WebClient for communicating with the Auth Service (via Nginx).
     *
     * This client is used to:
     * - Retrieve current user information
     * - Verify authentication status
     * - Get user details for personalization
     */
    private final WebClient authClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Nginx: As a gateway to the Auth Service
     */
    public InventoryController() {
        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
    }

    /**
     * Displays the main inventory page.
     *
     * This endpoint:
     * - Retrieves all inventory items from the Data Access Service
     * - Gets user information for personalization (if authenticated)
     * - Renders the inventory view with current stock levels
     *
     * Features:
     * - Current inventory levels for all products
     * - Stock quantity information
     * - Personalized experience for logged-in users
     *
     * @GetMapping - Maps HTTP GET requests to "/"
     * @param sessionId - The JSESSIONID cookie for authentication (optional)
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("inventory")
     */
    @GetMapping("/")
    public String home(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current user info from the auth service for personalization
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                // Request user information from Auth Service
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                // Add user info to model for personalization
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info if authentication fails
                // This should not happen in normal operation since Nginx enforces authentication
            }
        }

        // Retrieve inventory items and add to model
        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());

        // Return the inventory view template
        return "inventory";
    }

    /**
     * Displays the farmers page for adding produce to inventory.
     *
     * This endpoint:
     * - Retrieves all inventory items from the Data Access Service
     * - Gets user information for personalization (if authenticated)
     * - Renders the farmers view with form for adding produce
     *
     * Features:
     * - Current inventory levels for all products
     * - Form for farmers to add new produce quantities
     * - Batch update capability for multiple products
     *
     * @GetMapping - Maps HTTP GET requests to "/farmers"
     * @param sessionId - The JSESSIONID cookie for authentication (optional)
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("farmers")
     */
    @GetMapping("/farmers")
    public String farmerPage(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current user info from the auth service for personalization
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                // Request user information from Auth Service
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                // Add user info to model for personalization
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info if authentication fails
                // This should not happen in normal operation since Nginx enforces authentication
            }
        }

        // Retrieve inventory items and add to model
        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());

        // Return the farmers view template
        return "farmers";
    }

    /**
     * Processes the addition of multiple produce items to inventory.
     *
     * This endpoint:
     * - Receives quantity updates from the farmers form
     * - Updates inventory quantities in the database
     * - Displays success or error messages
     * - Re-renders the farmers view with updated inventory
     *
     * Transaction Flow:
     * 1. Receive quantity updates from form submission
     * 2. Process updates through the InventoryService
     * 3. Update inventory quantities in the database
     * 4. Display success or error message
     * 5. Show updated inventory
     *
     * Error Handling:
     * - Validates input quantities (non-negative, numeric)
     * - Catches and displays exceptions from the update process
     * - Provides specific error messages to the user
     *
     * @PostMapping - Maps HTTP POST requests to "/farmers/add-multiple"
     * @param sessionId - The JSESSIONID cookie for authentication (optional)
     * @param quantities - Map of product IDs to quantities from form submission
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("farmers")
     */
    @PostMapping("/farmers/add-multiple")
    public String addMultipleProduce(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam Map<String, String> quantities,
            Model model) {
        // Get the current user info from the auth service for personalization
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                // Request user information from Auth Service
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                // Add user info to model for personalization
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info if authentication fails
                // This should not happen in normal operation since Nginx enforces authentication
            }
        }

        try {
            // Process inventory additions through the service
            inventoryService.addMultipleProduce(quantities);

            // Add success message to the model
            model.addAttribute("success", true);

            // Retrieve updated inventory items with new quantities
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());

            // Return to the farmers view with success message
            return "farmers";
        } catch (Exception e) {
            // Add error message to the model if update fails
            model.addAttribute("error", "Failed to add produce: " + e.getMessage());

            // Retrieve current inventory items
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());

            // Return to the farmers view with error message
            return "farmers";
        }
    }

    // Price management has been moved to the admin service
}
