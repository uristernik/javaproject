package com.example.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8086");
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
