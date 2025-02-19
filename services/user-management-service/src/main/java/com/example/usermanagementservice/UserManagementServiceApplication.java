package com.example.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class UserManagementServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8083");
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }
}

@RestController
class UserManagementServiceApplicationController {
    @GetMapping("/")
    public String getInfo() {
        return "UserManagementServiceApplication Running!";
    }
}
