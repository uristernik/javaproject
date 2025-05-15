package com.example.inventoryservice.service;

/**
 * Inventory Service - Service Layer
 *
 * This service handles inventory-related business logic, including:
 * - Retrieving inventory data from the Data Access Service
 * - Processing inventory additions from farmers
 * - Managing stock levels
 * - Updating product prices
 *
 * Architecture Notes:
 * - This service encapsulates business logic separate from the controller
 * - It communicates with the Data Access Service for database operations
 * - It handles data transformation between API and domain models
 *
 * Key Responsibilities:
 * - Retrieving inventory information
 * - Processing inventory additions
 * - Managing stock levels
 * - Updating product prices
 *
 * In our microservices architecture:
 * - This service focuses on inventory business logic
 * - The Data Access Service handles the actual database operations
 * - The controller handles HTTP concerns and view rendering
 */

import com.example.inventoryservice.model.InventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for inventory management functionality.
 *
 * @Service - Indicates that this class is a Spring service component
 *            that contains business logic.
 */
@Service
public class InventoryService {
    /**
     * WebClient for communicating with the Data Access Service.
     *
     * This client is used to:
     * - Retrieve inventory data from the database
     * - Update inventory quantities
     * - Update product prices
     */
    private final WebClient webClient;

    /**
     * Constructor that initializes the WebClient instance.
     *
     * The WebClient is configured to communicate with:
     * - Data Access Service: For database operations
     */
    public InventoryService() {
        // Create WebClient for Data Access Service
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    /**
     * Retrieves all inventory items from the database.
     *
     * This method:
     * - Fetches inventory data from the Data Access Service
     * - Transforms the raw data into InventoryItem domain objects
     * - Returns a list of inventory items for display and processing
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
     * Adds a quantity of produce to a specific product's inventory.
     *
     * This method:
     * 1. Retrieves the current stock level for the product
     * 2. Calculates the new stock level by adding the quantity
     * 3. Updates the inventory through the Data Access Service
     *
     * @param productId The ID of the product to update
     * @param quantityToAdd The quantity (in kg) to add to the current stock
     * @throws RuntimeException if the product is not found
     */
    public void addProduce(Long productId, Double quantityToAdd) {
        // Get current stock
        InventoryItem currentItem = getInventoryItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Calculate new stock
        double newStock = currentItem.getStockKG() + quantityToAdd;

        // Update stock through data-access-service
        webClient.post()
                .uri("/api/data/tables/inventory/update")
                .bodyValue(Map.of(
                    "productId", productId,
                    "stockKG", newStock
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
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
     * Adds multiple produce quantities to inventory in a batch operation.
     *
     * This method:
     * 1. Parses and validates the input quantities from the form submission
     * 2. Retrieves current inventory levels
     * 3. Calculates new stock levels for each product
     * 4. Updates the inventory through the Data Access Service
     *
     * Form Parameter Format:
     * - Keys are in the format "quantities[productId]"
     * - Values are the quantities to add as strings
     *
     * @param quantities Map of form parameters containing product IDs and quantities
     * @throws RuntimeException if no valid quantities are provided or a product is not found
     */
    public void addMultipleProduce(Map<String, String> quantities) {
        // Filter out empty or zero quantities and parse the input format
        Map<Long, Double> validQuantities = quantities.entrySet().stream()
            // Filter keys that match the expected format
            .filter(entry -> entry.getKey().startsWith("quantities[") &&
                           entry.getKey().endsWith("]"))
            // Extract product ID and quantity from the key-value pairs
            .map(entry -> {
                Long productId = Long.parseLong(entry.getKey()
                    .replace("quantities[", "")
                    .replace("]", ""));
                Double quantity = Double.parseDouble(entry.getValue());
                return new AbstractMap.SimpleEntry<>(productId, quantity);
            })
            // Filter out zero or negative quantities
            .filter(entry -> entry.getValue() > 0)
            // Convert to a map of product ID to quantity
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Validate that there are quantities to process
        if (validQuantities.isEmpty()) {
            throw new RuntimeException("No valid quantities provided");
        }

        // Get current inventory items for efficient lookup
        Map<Long, InventoryItem> currentItems = getInventoryItems().stream()
            .collect(Collectors.toMap(InventoryItem::getProductId, item -> item));

        // Process each product update
        for (Map.Entry<Long, Double> entry : validQuantities.entrySet()) {
            Long productId = entry.getKey();
            Double quantityToAdd = entry.getValue();

            // Verify the product exists
            InventoryItem currentItem = currentItems.get(productId);
            if (currentItem == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            // Calculate new stock level
            double newStock = currentItem.getStockKG() + quantityToAdd;

            // Update inventory through data-access-service
            webClient.post()
                    .uri("/api/data/tables/inventory/update")
                    .bodyValue(Map.of(
                        "productId", productId,
                        "stockKG", newStock
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
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
