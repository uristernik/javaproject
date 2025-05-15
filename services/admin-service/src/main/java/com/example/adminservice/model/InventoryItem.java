package com.example.adminservice.model;

/**
 * Inventory Item Model
 *
 * This class represents an inventory item in the system and is used for:
 * - Displaying product information in the admin interface
 * - Managing product pricing
 * - Viewing current stock levels
 *
 * In our microservices architecture:
 * - This model is used by the Admin Service for price management
 * - It corresponds to the 'inventory' table in the database
 * - It's populated from data retrieved from the Data Access Service
 * - It's used primarily for the price management functionality
 * - Administrators can update prices but not stock levels through this interface
 */
public class InventoryItem {
    /**
     * Unique identifier for the product
     */
    private Long productId;

    /**
     * Description of the product
     */
    private String description;

    /**
     * Current stock level in kilograms
     * This is displayed for reference but not editable in the admin interface
     */
    private Double stockKG;

    /**
     * Price per kilogram in cents
     * This is the primary field that administrators can modify
     */
    private Integer pricePerKG;

    /**
     * Default constructor
     * Required for object creation and mapping from database
     */
    public InventoryItem() {}

    // Getters and Setters

    /**
     * Gets the product ID
     * @return The unique identifier for the product
     */
    public Long getProductId() { return productId; }

    /**
     * Sets the product ID
     * @param productId The unique identifier for the product
     */
    public void setProductId(Long productId) { this.productId = productId; }

    /**
     * Gets the product description
     * @return The description of the product
     */
    public String getDescription() { return description; }

    /**
     * Sets the product description
     * @param description The description of the product
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the current stock level in kilograms
     * @return The stock quantity in kilograms
     */
    public Double getStockKG() { return stockKG; }

    /**
     * Sets the stock level in kilograms
     * @param stockKG The stock quantity in kilograms
     */
    public void setStockKG(Double stockKG) { this.stockKG = stockKG; }

    /**
     * Gets the price per kilogram in cents
     * @return The price per kilogram in cents
     */
    public Integer getPricePerKG() { return pricePerKG; }

    /**
     * Sets the price per kilogram in cents
     * @param pricePerKG The price per kilogram in cents
     */
    public void setPricePerKG(Integer pricePerKG) { this.pricePerKG = pricePerKG; }
}
