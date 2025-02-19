package com.example.ordermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderManagementServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8084");
        SpringApplication.run(OrderManagementServiceApplication.class, args);
    }
}

@RestController
class OrderManagementServiceApplicationController {
    @GetMapping("/")
    public String getInfo() {
        return "OrderManagementServiceApplication Running!";
    }
}
