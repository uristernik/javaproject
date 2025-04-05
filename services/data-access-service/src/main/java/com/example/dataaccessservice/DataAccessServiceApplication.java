package com.example.dataaccessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DataAccessServiceApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8085");
        SpringApplication.run(DataAccessServiceApplication.class, args);
    }
}
