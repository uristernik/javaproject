package com.example.adminservice.controller;

import com.example.adminservice.model.User;
import com.example.adminservice.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, Model model) {
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
    public String showResetPasswordForm(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "reset-password";
    }

    @PostMapping("/users/{userId}/reset-password")
    public String resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword,
            Model model) {

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
