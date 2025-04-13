package com.example.adminservice.service;

import com.example.adminservice.model.InventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceManagementService {
    private final WebClient webClient;

    public PriceManagementService() {
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
        item.setStockKG(((Number) map.get("stockkg")).doubleValue());
        item.setPricePerKG(((Number) map.get("priceperkg")).intValue());
        return item;
    }

    public void updatePrices(Map<String, String> prices) {
        Map<Long, Integer> validPrices = prices.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("prices[") &&
                           entry.getKey().endsWith("]"))
            .map(entry -> {
                Long productId = Long.parseLong(entry.getKey()
                    .replace("prices[", "")
                    .replace("]", ""));
                Integer price = Integer.parseInt(entry.getValue());
                return new AbstractMap.SimpleEntry<>(productId, price);
            })
            .filter(entry -> entry.getValue() > 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (validPrices.isEmpty()) {
            throw new RuntimeException("No valid prices provided");
        }

        for (Map.Entry<Long, Integer> entry : validPrices.entrySet()) {
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
