package com.example.adminservice.controller;

/**
 * Admin Service - Price Management Controller
 *
 * This controller handles administrative price management functionality, including:
 * - Viewing current product prices
 * - Updating product prices
 * - Managing price lists
 *
 * Architecture Notes:
 * - The Admin Service provides administrative interfaces
 * - It communicates with the Data Access Service for inventory data operations via PriceManagementService
 * - It communicates with the Auth Service for current user information via PriceManagementService
 * - It requires admin privileges (user type=2) to access
 *
 * Key Responsibilities:
 * - Providing price management interfaces for administrators
 * - Handling HTTP requests and responses
 * - Delegating business logic to the service layer
 * - Rendering appropriate views
 *
 * In our microservices architecture:
 * - This service is protected by Nginx's auth_request directive
 * - The Auth Service verifies admin privileges
 * - The Data Access Service handles the actual database operations
 * - Price updates affect the product catalog immediately
 */

import com.example.adminservice.service.PriceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for price management functionality.
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */
@Controller
public class PriceManagementController {

    /**
     * Service that handles price management business logic.
     *
     * This service:
     * - Communicates with the Data Access Service
     * - Communicates with the Auth Service
     * - Retrieves inventory items with prices
     * - Updates product prices
     * - Retrieves user information
     * - Implements business rules for price management
     *
     * @Autowired - Injects the PriceManagementService bean into this controller
     */
    @Autowired
    private PriceManagementService priceManagementService;

    /**
     * Displays the price management page with current product prices.
     *
     * This endpoint:
     * - Retrieves all inventory items with prices from the Data Access Service
     * - Gets current admin user information for the UI
     * - Renders the price management view
     *
     * Features:
     * - List of all products with current prices
     * - Form for updating prices
     * - Real-time price management
     *
     * Security:
     * - Protected by Nginx's auth_request directive
     * - Only accessible to admin users (type=2)
     * - Returns 403 Forbidden for non-admin users
     *
     * @GetMapping - Maps HTTP GET requests to "/prices"
     * @param sessionId - The JSESSIONID cookie for authentication
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("manage-prices")
     */
    @GetMapping("/prices")
    public String managePrices(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current admin user info from the service
        Map<String, Object> userInfo = priceManagementService.getCurrentUserInfo(sessionId);
        if (userInfo != null) {
            // Add admin user info to model for UI personalization
            model.addAttribute("userInfo", userInfo);
        }

        // Retrieve all inventory items with prices
        model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());

        // Return the price management view template
        return "manage-prices";
    }

    /**
     * Processes price updates for products.
     *
     * This endpoint:
     * - Receives updated prices from the form submission
     * - Updates product prices in the database
     * - Displays success or error messages
     * - Re-renders the price management view
     *
     * Transaction Flow:
     * 1. Receive price updates from form submission
     * 2. Process updates through the PriceManagementService
     * 3. Display success or error message
     * 4. Show updated prices
     *
     * Error Handling:
     * - Catches and displays exceptions from the update process
     * - Provides specific error messages to the admin
     *
     * @PostMapping - Maps HTTP POST requests to "/prices/update"
     * @param sessionId - The JSESSIONID cookie for authentication
     * @param prices - Map of product IDs to new prices from form submission
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("manage-prices")
     */
    @PostMapping("/prices/update")
    public String updatePrices(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam Map<String, String> prices,
            Model model) {
        // Get the current admin user info from the service
        Map<String, Object> userInfo = priceManagementService.getCurrentUserInfo(sessionId);
        if (userInfo != null) {
            // Add admin user info to model for UI personalization
            model.addAttribute("userInfo", userInfo);
        }

        try {
            // Process price updates through the service
            priceManagementService.updatePrices(prices);

            // Add success message to the model
            model.addAttribute("success", "Prices updated successfully!");

            // Retrieve updated inventory items with new prices
            model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());

            // Return to the price management view
            return "manage-prices";
        } catch (Exception e) {
            // Add error message to the model if update fails
            model.addAttribute("error", "Failed to update prices: " + e.getMessage());

            // Retrieve current inventory items
            model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());

            // Return to the price management view with error message
            return "manage-prices";
        }
    }
}
