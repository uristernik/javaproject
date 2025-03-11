package com.example.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8083");
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }
}
