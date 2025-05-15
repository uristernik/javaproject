package com.example.productcatalogservice.model;

/**
 * Product Model
 * 
 * This class represents a product in the system and is used for:
 * - Displaying product information in the catalog
 * - Managing product details and pricing
 * - Storing product data for checkout
 * 
 * In our microservices architecture:
 * - This model is used by the Product Catalog Service for product display
 * - It corresponds to the 'inventory' table in the database
 * - It's populated from data retrieved from the Data Access Service
 * - It's used for both display and business logic
 */
public class Product {
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
     */
    private Double stockKG;
    
    /**
     * Price per kilogram in cents
     */
    private Integer pricePerKG;
    
    /**
     * URL to the product image
     */
    private String imageUrl;

    /**
     * Default constructor
     * Required for object creation and mapping from database
     */
    public Product() {}

    /**
     * Gets the product ID
     * @return The unique identifier for the product
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the product ID
     * @param productId The unique identifier for the product
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Gets the product description
     * @return The description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description
     * @param description The description of the product
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the current stock level in kilograms
     * @return The stock quantity in kilograms
     */
    public Double getStockKG() {
        return stockKG;
    }

    /**
     * Sets the stock level in kilograms
     * @param stockKG The stock quantity in kilograms
     */
    public void setStockKG(Double stockKG) {
        this.stockKG = stockKG;
    }

    /**
     * Gets the price per kilogram in cents
     * @return The price per kilogram in cents
     */
    public Integer getPricePerKG() {
        return pricePerKG;
    }

    /**
     * Sets the price per kilogram in cents
     * @param pricePerKG The price per kilogram in cents
     */
    public void setPricePerKG(Integer pricePerKG) {
        this.pricePerKG = pricePerKG;
    }

    /**
     * Gets the product image URL
     * @return The URL to the product image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the product image URL
     * @param imageUrl The URL to the product image
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
