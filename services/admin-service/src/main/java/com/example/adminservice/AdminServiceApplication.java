package com.example.adminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}

@RestController
@RequestMapping
class AdminServiceApplicationController {
    @GetMapping(value = {"", "/", "/admin", "/admin/"})
    public String getInfo() {
        return "AdminServiceApplication Running!";
    }
}
