package com.example.ordermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderManagementServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8084");
        SpringApplication.run(OrderManagementServiceApplication.class, args);
    }
}
