package com.example.adminservice.controller;

import com.example.adminservice.model.User;
import com.example.adminservice.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    private final WebClient authClient;

    public UserManagementController() {
        this.authClient = WebClient.create("http://nginx:80");
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String listUsers(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            Model model) {
        // Get the current user info from the auth service
        Map<String, Object> userInfo = null;
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        // Check if the user is trying to delete itself
        if (userInfo != null && userInfo.containsKey("id")) {
            Number idNumber = (Number) userInfo.get("id");
            if (idNumber != null) {
                Long currentUserId = idNumber.longValue();
                if (currentUserId.equals(userId)) {
                    model.addAttribute("errorMessage", "You cannot delete your own account");
                    List<User> users = userManagementService.getAllUsers();
                    model.addAttribute("users", users);
                    return "users";
                }
            }
        }

        boolean success = userManagementService.deleteUser(userId);

        if (success) {
            model.addAttribute("successMessage", "User deleted successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to delete user");
        }

        // Get the updated list of users and return the users page directly
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{userId}/reset-password")
    public String showResetPasswordForm(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        model.addAttribute("userId", userId);
        return "reset-password";
    }

    @PostMapping("/users/{userId}/reset-password")
    public String resetPassword(
            @CookieValue(name = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long userId,
            @RequestParam String newPassword,
            Model model) {
        // Get the current user info from the auth service
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Map<String, Object> userInfo = authClient.get()
                        .uri("/auth/user")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                model.addAttribute("userInfo", userInfo);
            } catch (Exception e) {
                // Continue without user info
            }
        }

        boolean success = userManagementService.resetPassword(userId, newPassword);

        if (success) {
            model.addAttribute("successMessage", "Password reset successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to reset password");
        }

        // Get the updated list of users and return the users page directly
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
}
