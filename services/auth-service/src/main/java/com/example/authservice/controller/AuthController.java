package com.example.authservice.controller;

/**
 * Auth Service - Controller Layer
 *
 * This controller handles authentication functionality, including:
 * - User login page rendering
 * - User registration
 * - Form processing for authentication
 *
 * Architecture Notes:
 * - The Auth Service is the central authority for authentication
 * - This controller handles the user-facing authentication interfaces
 * - It delegates business logic to the UserService
 * - It works with Spring Security for authentication processing
 *
 * Key Responsibilities:
 * - Rendering login and registration forms
 * - Processing registration submissions
 * - Handling authentication errors
 * - Redirecting users after successful authentication
 *
 * In our microservices architecture:
 * - This controller handles the user-facing authentication interfaces
 * - The AuthCheckController handles internal authentication verification
 * - The UserService handles user data operations
 * - The SecurityConfig configures Spring Security for authentication
 */

import com.example.authservice.model.User;
import com.example.authservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for authentication functionality.
 *
 * This controller handles:
 * - Login page rendering
 * - User registration form display
 * - User registration processing
 *
 * @Controller - Indicates that this class serves as a Spring MVC controller,
 *               handling HTTP requests and returning views.
 */
@Controller
public class AuthController {

    /**
     * Service that handles user-related business logic.
     *
     * This service:
     * - Registers new users
     * - Validates user data
     * - Communicates with the Data Access Service
     *
     * @Autowired - Injects the UserService bean into this controller
     */
    @Autowired
    private UserService userService;

    /**
     * Displays the login page.
     *
     * This endpoint:
     * - Renders the login form
     * - The actual login processing is handled by Spring Security
     * - No model attributes are needed as Spring Security provides the form
     *
     * URL: /login
     * HTTP Method: GET
     *
     * @return The name of the view template to render ("login")
     */
    @GetMapping("/login")
    public String loginPage() {
        // Return the login view template
        return "login";
    }

    /**
     * Displays the user registration form.
     *
     * This endpoint:
     * - Renders the registration form
     * - Adds an empty User object to the model for form binding
     *
     * URL: /register
     * HTTP Method: GET
     *
     * @param model The Spring MVC model for passing data to the view
     * @return The name of the view template to render ("register")
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Add empty user object to the model for form binding
        model.addAttribute("user", new User());
        // Return the registration form view
        return "register";
    }

    /**
     * Processes user registration submissions.
     *
     * This endpoint:
     * 1. Validates the submitted user data
     * 2. Registers the new user if validation passes
     * 3. Redirects to login page on success
     * 4. Returns to registration form with errors if validation fails
     *
     * URL: /register
     * HTTP Method: POST
     *
     * @param user The User object populated from form submission
     * @param result BindingResult containing validation errors, if any
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect to login page on success, or back to registration form on error
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        // If validation errors exist, return to the registration form
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Attempt to register the new user
            userService.registerNewUser(user);
            // Add success message for display after redirect
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            // Redirect to login page
            return "redirect:/login";
        } catch (Exception e) {
            // Add error message for display after redirect
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Redirect back to registration page
            return "redirect:/register";
        }
    }
}
