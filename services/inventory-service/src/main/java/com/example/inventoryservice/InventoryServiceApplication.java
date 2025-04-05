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
                                    RedirectAttributes redirectAttributes) {
        try {
            inventoryService.addMultipleProduce(quantities);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/farmers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add produce: " + e.getMessage());
            return "redirect:/farmers";
        }
    }
}
