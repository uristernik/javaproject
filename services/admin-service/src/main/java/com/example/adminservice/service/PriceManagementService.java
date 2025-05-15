package com.example.adminservice.service;

/**
 * Price Management Service - Service Layer
 *
 * This service handles price management business logic, including:
 * - Retrieving inventory data from the Data Access Service
 * - Processing price updates from administrators
 * - Managing product pricing
 *
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles data transformation between API and domain models
 *
 * Key Responsibilities:
 * - Retrieving inventory information for price management
 * - Processing price updates
 * - Validating price data
 *
 * In our microservices architecture:
 * - This service focuses on price management business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 * - Only administrators can access this functionality
 */

import com.example.adminservice.model.InventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for price management functionality.
 *
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class PriceManagementService {
    /**
     * WebClient for communicating with the Data Access Service.
     *
     * This client is used to:
     * - Retrieve inventory data from the database
     * - Update product prices
     */
    private final WebClient webClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public PriceManagementService() {
        // Create WebClient for Data Access Service
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    /**
     * Retrieves all inventory items from the database.
     *
     * This method:
     * - Fetches inventory data from the Data Access Service
     * - Transforms the raw data into InventoryItem domain objects
     * - Returns a list of inventory items for display and price management
     *
     * @return List of InventoryItem objects representing the current inventory
     */
    public List<InventoryItem> getInventoryItems() {
        // Retrieve inventory data from Data Access Service
        return webClient.get()
                .uri("/api/data/tables/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block()
                // Transform raw data into domain objects
                .stream()
                .map(this::mapToInventoryItem)
                .collect(Collectors.toList());
    }

    /**
     * Maps a raw data map from the database to an InventoryItem domain object.
     *
     * This method:
     * - Extracts values from the raw data map
     * - Converts them to the appropriate types
     * - Populates an InventoryItem object with the values
     *
     * @param map The raw data map from the database
     * @return An InventoryItem object populated with the data
     */
    private InventoryItem mapToInventoryItem(Map<String, Object> map) {
        InventoryItem item = new InventoryItem();
        // Extract and convert product ID
        item.setProductId(((Number) map.get("productid")).longValue());
        // Extract product description
        item.setDescription((String) map.get("description"));
        // Extract and convert stock quantity
        item.setStockKG(((Number) map.get("stockkg")).doubleValue());
        // Extract and convert price per kg
        item.setPricePerKG(((Number) map.get("priceperkg")).intValue());
        return item;
    }

    /**
     * Updates prices for multiple products in a batch operation.
     *
     * This method:
     * 1. Parses and validates the input prices from the form submission
     * 2. Updates each product's price through the Data Access Service
     *
     * Form Parameter Format:
     * - Keys are in the format "prices[productId]"
     * - Values are the new prices as strings
     *
     * @param prices Map of form parameters containing product IDs and prices
     * @throws RuntimeException if no valid prices are provided
     */
    public void updatePrices(Map<String, String> prices) {
        // Parse and validate the input prices
        Map<Long, Integer> validPrices = prices.entrySet().stream()
            // Filter keys that match the expected format
            .filter(entry -> entry.getKey().startsWith("prices[") &&
                           entry.getKey().endsWith("]"))
            // Extract product ID and price from the key-value pairs
            .map(entry -> {
                Long productId = Long.parseLong(entry.getKey()
                    .replace("prices[", "")
                    .replace("]", ""));
                Integer price = Integer.parseInt(entry.getValue());
                return new AbstractMap.SimpleEntry<>(productId, price);
            })
            // Filter out zero or negative prices
            .filter(entry -> entry.getValue() > 0)
            // Convert to a map of product ID to price
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Validate that there are prices to update
        if (validPrices.isEmpty()) {
            throw new RuntimeException("No valid prices provided");
        }

        // Update each product's price
        for (Map.Entry<Long, Integer> entry : validPrices.entrySet()) {
            // Update price through data-access-service
            webClient.post()
                    .uri("/api/data/tables/inventory/update")
                    .bodyValue(Map.of(
                        "productId", entry.getKey(),
                        "pricePerKG", entry.getValue()
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }
}
