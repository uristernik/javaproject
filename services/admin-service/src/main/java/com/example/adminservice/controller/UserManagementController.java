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
        return "redirect:/users";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    
    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        boolean success = userManagementService.deleteUser(userId);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user");
        }
        
        return "redirect:/users";
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
            RedirectAttributes redirectAttributes) {
        
        boolean success = userManagementService.resetPassword(userId, newPassword);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reset password");
        }
        
        return "redirect:/users";
    }
}
