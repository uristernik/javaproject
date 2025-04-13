package com.example.inventoryservice.model;

public class InventoryItem {
    private Long productId;
    private String description;
    private Double stockKG;
    private Integer pricePerKG;

    // Default constructor
    public InventoryItem() {}

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getStockKG() { return stockKG; }
    public void setStockKG(Double stockKG) { this.stockKG = stockKG; }

    public Integer getPricePerKG() { return pricePerKG; }
    public void setPricePerKG(Integer pricePerKG) { this.pricePerKG = pricePerKG; }
}
