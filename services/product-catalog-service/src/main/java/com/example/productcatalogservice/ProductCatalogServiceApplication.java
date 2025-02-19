package com.example.productcatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ProductCatalogServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8082");
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }
}

@RestController
class ProductCatalogServiceApplicationController {
    @GetMapping("/")
    public String getInfo() {
        return "ProductCatalogServiceApplication Running!";
    }
}
