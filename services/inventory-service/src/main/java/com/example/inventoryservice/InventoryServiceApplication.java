package com.example.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

@RestController
class InventoryServiceApplicationController {
    @GetMapping("/")
    public String getInfo() {
        return "InventoryServiceApplication Running!";
    }
}
