package com.example.inventoryservice.model;

public class InventoryItem {
    private Long productId;
    private String description;
    private Integer quantityKG;
    private Integer stockKG;
    private Integer pricePerKG;

    // Default constructor
    public InventoryItem() {}

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getQuantityKG() { return quantityKG; }
    public void setQuantityKG(Integer quantityKG) { this.quantityKG = quantityKG; }
    
    public Integer getStockKG() { return stockKG; }
    public void setStockKG(Integer stockKG) { this.stockKG = stockKG; }
    
    public Integer getPricePerKG() { return pricePerKG; }
    public void setPricePerKG(Integer pricePerKG) { this.pricePerKG = pricePerKG; }
}
