package com.example.ordermanagementservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    private final WebClient webClient;

    public OrderController() {
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        // For now, hardcode userId as 1
        Long userId = 1L;

        // Fetch orders from data-access-service
        List<Map<String, Object>> orders = webClient.get()
                .uri("/api/data/orders/user/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        model.addAttribute("orders", orders);
        return "orders";
    }
}
