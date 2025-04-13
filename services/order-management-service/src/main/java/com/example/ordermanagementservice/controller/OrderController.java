package com.example.ordermanagementservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    private final WebClient dataAccessClient;
    private final WebClient authClient;

    public OrderController() {
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/orders")
    public String showOrders(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {

        if (sessionId == null || sessionId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Get the current user ID from the auth service through nginx
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated: " + e.getMessage());
        }

        if (userInfo == null || !userInfo.containsKey("id")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long userId = ((Number) userInfo.get("id")).longValue();

        // Fetch orders from data-access-service for the current user
        List<Map<String, Object>> orders = dataAccessClient.get()
                .uri("/api/data/orders/user/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        model.addAttribute("orders", orders);
        model.addAttribute("userInfo", userInfo);
        return "orders";
    }
}
