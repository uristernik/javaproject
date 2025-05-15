package com.example.productcatalogservice.controller;

/**
 * Product Catalog Service - Controller Layer
 *
 * This controller handles the product catalog functionality, including:
 * - Displaying the product catalog
 * - Managing the checkout process
 * - Rendering the home page
 *
 * Architecture Notes:
 * - The Product Catalog Service is customer-facing
 * - It communicates with the Data Access Service for product data via ProductService
 * - It communicates with the Auth Service for user information
 * - It delegates business logic to the ProductService
 *
 * Key Responsibilities:
 * - Handling HTTP requests and responses
 * - Managing view rendering and model attributes
 * - Delegating business logic to the service layer
 * - Providing a personalized user experience
 *
 * In our microservices architecture:
 * - This controller handles the presentation layer
 * - The ProductService handles business logic
 * - The Data Access Service handles database operations
 * - The Order Management Service handles order fulfillment
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import com.example.productcatalogservice.service.ProductService;

import java.util.List;
import java.util.Map;

/**
 * Controller for product catalog and checkout functionality.
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */
@Controller
public class ProductCatalogController {
    /**
     * ProductService for handling business logic.
     *
     * This service:
     * - Encapsulates product-related business logic
     * - Communicates with the Data Access Service
     * - Handles checkout processing and order creation
     *
     * @Autowired - Injects the ProductService bean into this controller
     */
    @Autowired
    private ProductService productService;

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
    public ProductCatalogController() {
        // Create WebClient for Auth Service (via Nginx)
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/")
    public String showRoot(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {

        if (sessionId == null || sessionId.isEmpty()) {
            return "redirect:/login";
        }

        // Get the current user info from the auth service
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            return "redirect:/login";
        }

        model.addAttribute("userInfo", userInfo);
        return "home";
    }

    @GetMapping("/home")
    public String showHome(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam(name = "error", required = false) String error,
            Model model) {

        if (sessionId == null || sessionId.isEmpty()) {
            return "redirect:/login";
        }

        // Get the current user info from the auth service
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            return "redirect:/login";
        }

        model.addAttribute("userInfo", userInfo);

        // Add error message if access was denied
        if (error != null && error.equals("access_denied")) {
            model.addAttribute("errorMessage", "Access denied. You do not have permission to access the requested page.");
        }

        return "home";
    }

    /**
     * Displays the product catalog page.
     *
     * This endpoint:
     * - Retrieves product data from the Data Access Service
     * - Gets user information for personalization (if authenticated)
     * - Renders the catalog view with product listings
     *
     * Features:
     * - Product listings with images, descriptions, and prices
     * - Stock availability information
     * - Add to basket functionality
     * - Personalized experience for logged-in users
     *
     * @GetMapping - Maps HTTP GET requests to "/catalog"
     * @param sessionId - The JSESSIONID cookie for authentication (optional)
     * @param model - The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("catalog")
     */
    @GetMapping("/catalog")
    public String showCatalog(
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
                // This allows the catalog to be viewed without requiring login
            }
        }

        // Retrieve product data using the ProductService
        List<Map<String, Object>> products = productService.getProducts();

        // Add products to model for display in the view
        model.addAttribute("products", products);

        // Return the catalog view template
        return "catalog";
    }

    /**
     * Processes the checkout operation.
     *
     * This endpoint:
     * 1. Receives checkout data from the client
     * 2. Updates inventory quantities
     * 3. Creates a new order
     * 4. Returns the order confirmation
     *
     * Transaction Flow:
     * 1. Update inventory quantities (reduce stock)
     * 2. Create order record with items
     * 3. Return order confirmation with order ID
     *
     * Error Handling:
     * - Returns 400 Bad Request with error message if any step fails
     * - Client can display appropriate error messages
     *
     * @PostMapping - Maps HTTP POST requests to "/checkout"
     * @ResponseBody - Indicates that the return value should be written to the response body
     * @param checkoutData - JSON data containing order details (items, user, address, etc.)
     * @return ResponseEntity with order confirmation or error message
     */
    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData) {
        try {
            // Delegate checkout processing to the ProductService
            Map<String, Object> result = productService.processCheckout(checkoutData);

            // Return order confirmation
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Return error response if any step fails
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/checkout")
    public String showCheckout(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {

        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        return "checkout";
    }
}
