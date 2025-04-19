package com.example.adminservice.controller;

import com.example.adminservice.service.PriceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Controller
public class PriceManagementController {

    @Autowired
    private PriceManagementService priceManagementService;

    private final WebClient authClient;

    public PriceManagementController() {
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/prices")
    public String managePrices(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());
        return "manage-prices";
    }

    @PostMapping("/prices/update")
    public String updatePrices(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam Map<String, String> prices,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        try {
            priceManagementService.updatePrices(prices);
            model.addAttribute("success", "Prices updated successfully!");
            // Instead of redirecting, render the page directly
            model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());
            return "manage-prices";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update prices: " + e.getMessage());
            model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());
            return "manage-prices";
        }
    }
}
