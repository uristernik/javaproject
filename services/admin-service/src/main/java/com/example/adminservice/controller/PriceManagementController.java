package com.example.adminservice.controller;

import com.example.adminservice.service.PriceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PriceManagementController {

    @Autowired
    private PriceManagementService priceManagementService;

    @GetMapping("/prices")
    public String managePrices(Model model) {
        model.addAttribute("inventoryItems", priceManagementService.getInventoryItems());
        return "manage-prices";
    }

    @PostMapping("/prices/update")
    public String updatePrices(@RequestParam Map<String, String> prices, Model model) {
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
