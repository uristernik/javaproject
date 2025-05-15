package com.example.productcatalogservice.service;

/**
 * Product Catalog Service - Service Layer
 * 
 * This service handles product-related business logic, including:
 * - Retrieving product data from the Data Access Service
 * - Processing checkout operations
 * - Managing product catalog information
 * 
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles transaction processing for orders
 * 
 * Key Responsibilities:
 * - Retrieving product catalog data
 * - Processing checkout and order creation
 * - Updating inventory during checkout
 * 
 * In our microservices architecture:
 * - This service focuses on product and order business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 */

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for product catalog and checkout functionality.
 * 
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class ProductService {
    
    /**
     * WebClient for communicating with the Data Access Service.
     * 
     * This client is used to:
     * - Retrieve product data from the database
     * - Create orders during checkout
     * - Update inventory quantities
     */
    private final WebClient dataAccessClient;
    
    /**
     * Constructor that initializes the WebClient instance.
     * 
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public ProductService() {
        // Create WebClient for Data Access Service
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");
    }
    
    /**
     * Retrieves all products from the inventory.
     * 
     * This method:
     * - Fetches product data from the Data Access Service
     * - Returns a list of products for display in the catalog
     * 
     * @return List of product records as maps
     */
    public List<Map<String, Object>> getProducts() {
        return dataAccessClient.get()
                .uri("/api/data/tables/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }
    
    /**
     * Processes a checkout operation.
     * 
     * This method:
     * 1. Updates inventory quantities (reduces stock)
     * 2. Creates a new order record
     * 3. Returns the order confirmation
     * 
     * Transaction Flow:
     * 1. Extract items from checkout data
     * 2. Update inventory quantities
     * 3. Create order record with items
     * 4. Return order confirmation
     * 
     * @param checkoutData Map containing order details (items, user, address, etc.)
     * @return Map containing the order confirmation (order ID, etc.)
     * @throws Exception if any step in the checkout process fails
     */
    public Map<String, Object> processCheckout(Map<String, Object> checkoutData) throws Exception {
        try {
            // Extract items from checkout data
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) checkoutData.get("items");
            
            // Step 1: Update inventory quantities
            // This reduces stock levels for purchased items
            dataAccessClient.post()
                .uri("/api/data/inventory/update-batch")
                .bodyValue(items)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            
            // Step 2: Create the order record
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("userId", checkoutData.get("userId"));
            orderData.put("deliveryAddress", checkoutData.get("deliveryAddress"));
            orderData.put("totalPrice", checkoutData.get("totalPrice"));
            orderData.put("items", items);
            
            // Send order creation request to Data Access Service
            return dataAccessClient.post()
                .uri("/api/data/orders/create")
                .bodyValue(orderData)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
        } catch (Exception e) {
            // Rethrow the exception to be handled by the controller
            throw new Exception("Checkout process failed: " + e.getMessage(), e);
        }
    }
}
