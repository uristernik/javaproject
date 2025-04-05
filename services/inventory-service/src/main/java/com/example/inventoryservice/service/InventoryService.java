package com.example.inventoryservice.service;

import com.example.inventoryservice.model.InventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.Map;
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

    private InventoryItem mapToInventoryItem(Map<String, Object> map) {
        InventoryItem item = new InventoryItem();
        item.setProductId(((Number) map.get("productid")).longValue());
        item.setDescription((String) map.get("description"));
        item.setQuantityKG(((Number) map.get("quantitykg")).intValue());
        item.setStockKG(((Number) map.get("stockkg")).intValue());
        item.setPricePerKG(((Number) map.get("priceperkg")).intValue());
        return item;
    }
}
