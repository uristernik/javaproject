package com.example.productcatalogservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class ProductCatalogController {
    private final WebClient dataAccessClient;
    private final WebClient authClient;

    public ProductCatalogController() {
        this.dataAccessClient = WebClient.create("http://data-access-service:8085");
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/")
    public String showRoot(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {

        if (sessionId == null || sessionId.isEmpty()) {
            return "redirect:/login";
        }

        // Get the current user info from the auth service
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            return "redirect:/login";
        }

        model.addAttribute("userInfo", userInfo);
        return "home";
    }

    @GetMapping("/home")
    public String showHome(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam(name = "error", required = false) String error,
            Model model) {

        if (sessionId == null || sessionId.isEmpty()) {
            return "redirect:/login";
        }

        // Get the current user info from the auth service
        Map<String, Object> userInfo;
        try {
            userInfo = authClient.get()
                    .uri("/auth/user")
                    .cookie("JSESSIONID", sessionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            return "redirect:/login";
        }

        model.addAttribute("userInfo", userInfo);

        // Add error message if access was denied
        if (error != null && error.equals("access_denied")) {
            model.addAttribute("errorMessage", "Access denied. You do not have permission to access the requested page.");
        }

        return "home";
    }

    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        List<Map<String, Object>> products = dataAccessClient.get()
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
            dataAccessClient.post()
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

            Map<String, Object> result = dataAccessClient.post()
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
