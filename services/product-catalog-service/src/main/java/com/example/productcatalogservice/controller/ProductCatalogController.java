package com.example.productcatalogservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

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
}
