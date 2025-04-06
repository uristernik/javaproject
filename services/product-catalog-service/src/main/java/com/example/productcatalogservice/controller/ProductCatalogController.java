package com.example.productcatalogservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class ProductCatalogController {
    private final WebClient webClient;

    public ProductCatalogController() {
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        List<Map<String, Object>> products = webClient.get()
                .uri("/api/data/tables/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
        
        model.addAttribute("products", products);
        return "catalog";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) checkoutData.get("items");
            
            // First, update inventory
            webClient.post()
                .uri("/api/data/inventory/update-batch")
                .bodyValue(items)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

            // Then create the order
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("userId", checkoutData.get("userId"));
            orderData.put("deliveryAddress", checkoutData.get("deliveryAddress"));
            orderData.put("totalPrice", checkoutData.get("totalPrice"));
            orderData.put("items", items);

            Map<String, Object> result = webClient.post()
                .uri("/api/data/orders/create")
                .bodyValue(orderData)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/checkout")
    public String showCheckout() {
        return "checkout";
    }
}
