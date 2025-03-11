package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.dto.AuthResponse;
import com.example.usermanagementservice.dto.SignInRequest;
import com.example.usermanagementservice.dto.SignUpRequest;
import com.example.usermanagementservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login-page")
    public String loginPage() {
        return "redirect:/login.html";
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}

