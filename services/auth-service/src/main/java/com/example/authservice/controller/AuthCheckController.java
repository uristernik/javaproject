package com.example.authservice.controller;

import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthCheckController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/check")
    public ResponseEntity<Void> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return ResponseEntity.ok().build(); // Return 200 OK if authenticated
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return 401 if not authenticated
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("firstName", user.getFirstName());
                userInfo.put("lastName", user.getLastName());
                userInfo.put("type", user.getType());

                return ResponseEntity.ok(userInfo);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
