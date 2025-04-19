package com.example.inventoryservice;

import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

@Controller
class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    private final WebClient authClient;

    public InventoryController() {
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/")
    public String home(
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

        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
        return "inventory";
    }

    @GetMapping("/farmers")
    public String farmerPage(
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

        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
        return "farmers";
    }

    @PostMapping("/farmers/add-multiple")
    public String addMultipleProduce(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @RequestParam Map<String, String> quantities,
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
            inventoryService.addMultipleProduce(quantities);
            model.addAttribute("success", true);
            // Instead of redirecting, render the farmers page directly
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
            return "farmers";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add produce: " + e.getMessage());
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
            return "farmers";
        }
    }

    // Price management has been moved to the admin service
}
