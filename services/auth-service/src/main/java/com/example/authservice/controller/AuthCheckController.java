package com.example.authservice.controller;

import com.example.authservice.model.User;
import com.example.authservice.service.DataAccessService;
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

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthCheckController {

    @Autowired
    private DataAccessService dataAccessService;

    @GetMapping("/check")
    public ResponseEntity<Void> checkAuth(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Get the original URI from the request headers
            String originalUri = request.getHeader("X-Original-URI");

            // If the request is for an admin route, check if the user is an admin
            if (originalUri != null && originalUri.startsWith("/admin/")) {
                String email = auth.getName();
                Optional<User> userOpt = dataAccessService.findUserByEmail(email);

                if (userOpt.isPresent() && userOpt.get().getType() != null && userOpt.get().getType() == 2) {
                    return ResponseEntity.ok().build(); // Return 200 OK if user is admin
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 if user is not admin
                }
            }

            return ResponseEntity.ok().build(); // Return 200 OK if authenticated for non-admin routes
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
            Optional<User> userOpt = dataAccessService.findUserByEmail(email);

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
