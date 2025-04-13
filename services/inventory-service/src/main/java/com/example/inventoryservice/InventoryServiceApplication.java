package com.example.inventoryservice;

import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
        return "inventory";
    }

    @GetMapping("/farmers")
    public String farmerPage(Model model) {
        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
        return "farmers";
    }

    @PostMapping("/farmers/add-multiple")
    public String addMultipleProduce(@RequestParam Map<String, String> quantities,
                                    Model model) {
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

    @GetMapping("/admin/prices")
    public String managePrices(Model model) {
        model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
        return "manage-prices";
    }

    @PostMapping("/admin/prices/update")
    public String updatePrices(@RequestParam Map<String, String> prices,
                             Model model) {
        try {
            inventoryService.updatePrices(prices);
            model.addAttribute("success", "Prices updated successfully!");
            // Instead of redirecting, render the page directly
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
            return "manage-prices";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update prices: " + e.getMessage());
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
            return "manage-prices";
        }
    }
}
