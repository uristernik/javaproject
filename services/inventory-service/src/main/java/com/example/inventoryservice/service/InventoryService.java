package com.example.inventoryservice.service;

import com.example.inventoryservice.model.InventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final WebClient webClient;

    public InventoryService() {
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    public List<InventoryItem> getInventoryItems() {
        return webClient.get()
                .uri("/api/data/tables/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block()
                .stream()
                .map(this::mapToInventoryItem)
                .collect(Collectors.toList());
    }

    public void addProduce(Long productId, Integer quantityKG) {
        // Get current quantity
        InventoryItem currentItem = getInventoryItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Calculate new quantities
        int newQuantity = currentItem.getQuantityKG() + quantityKG;
        int newStock = currentItem.getStockKG() + quantityKG;
        
        // Update both quantity and stock through data-access-service
        webClient.post()
                .uri("/api/data/tables/inventory/update")
                .bodyValue(Map.of(
                    "productId", productId,
                    "quantityKG", newQuantity,
                    "stockKG", newStock
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private InventoryItem mapToInventoryItem(Map<String, Object> map) {
        InventoryItem item = new InventoryItem();
        item.setProductId(((Number) map.get("productid")).longValue());
        item.setDescription((String) map.get("description"));
        item.setQuantityKG(((Number) map.get("quantitykg")).intValue());
        item.setStockKG(((Number) map.get("stockkg")).intValue());
        item.setPricePerKG(((Number) map.get("priceperkg")).intValue());
        return item;
    }

    public void addMultipleProduce(Map<String, String> quantities) {
        // Filter out empty or zero quantities
        Map<Long, Integer> validQuantities = quantities.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("quantities[") && 
                           entry.getKey().endsWith("]"))
            .map(entry -> {
                Long productId = Long.parseLong(entry.getKey()
                    .replace("quantities[", "")
                    .replace("]", ""));
                Integer quantity = Integer.parseInt(entry.getValue());
                return new AbstractMap.SimpleEntry<>(productId, quantity);
            })
            .filter(entry -> entry.getValue() > 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (validQuantities.isEmpty()) {
            throw new RuntimeException("No valid quantities provided");
        }

        // Get current inventory items
        Map<Long, InventoryItem> currentItems = getInventoryItems().stream()
            .collect(Collectors.toMap(InventoryItem::getProductId, item -> item));

        // Update each product
        for (Map.Entry<Long, Integer> entry : validQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantityToAdd = entry.getValue();
            
            InventoryItem currentItem = currentItems.get(productId);
            if (currentItem == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            // Calculate new quantities
            int newQuantity = currentItem.getQuantityKG() + quantityToAdd;
            int newStock = currentItem.getStockKG() + quantityToAdd;

            // Update through data-access-service
            webClient.post()
                    .uri("/api/data/tables/inventory/update")
                    .bodyValue(Map.of(
                        "productId", productId,
                        "quantityKG", newQuantity,
                        "stockKG", newStock
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }
}
