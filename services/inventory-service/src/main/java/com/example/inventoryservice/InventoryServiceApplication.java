package com.example.inventoryservice;

import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/farmers/add")
    public String addProduce(@RequestParam Long productId,
                            @RequestParam Integer quantityKG,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            inventoryService.addProduce(productId, quantityKG);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("inventoryItems", inventoryService.getInventoryItems());
            model.addAttribute("error", "Failed to add produce: " + e.getMessage());
            return "farmers";
        }
        return "redirect:/farmers";
    }
}
