package com.example.authservice.config;

/**
 * Auth Service - Security Configuration
 *
 * This class is part of the Auth Service, which is responsible for user authentication,
 * registration, and session management in the microservices architecture.
 *
 * Architecture Notes:
 * - The Auth Service acts as the security gatekeeper for the entire application
 * - It uses Spring Security for authentication and authorization
 * - It communicates with the Data Access Service to retrieve user credentials
 * - The Nginx reverse proxy routes authentication requests to this service
 *
 * Key Libraries Used:
 * - Spring Security: Provides authentication, authorization, and protection against common attacks
 * - BCrypt: Industry-standard password hashing algorithm for secure password storage
 *
 * Authentication Flow:
 * 1. User submits login credentials to /login endpoint
 * 2. Spring Security processes the authentication request
 * 3. CustomUserDetailsService loads the user from the Data Access Service
 * 4. Credentials are verified using BCryptPasswordEncoder
 * 5. Upon successful authentication, a session is created (JSESSIONID cookie)
 * 6. User is redirected to the default success URL
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @Configuration: Indicates that this class contains Spring bean definitions
 * @EnableWebSecurity: Enables Spring Security's web security support
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * UserDetailsService is a core interface in Spring Security that loads user-specific data.
     *
     * In our microservices architecture:
     * - This is implemented by CustomUserDetailsService
     * - It communicates with the Data Access Service to retrieve user information
     * - It converts database user records into Spring Security's UserDetails objects
     *
     * @Autowired - Injects the UserDetailsService implementation into this configuration class
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Creates a PasswordEncoder bean for secure password handling.
     *
     * BCryptPasswordEncoder:
     * - Implements the PasswordEncoder interface
     * - Uses the BCrypt strong hashing function
     * - Automatically handles salt generation and inclusion in the hash
     * - Industry standard for secure password storage
     *
     * @Bean - Indicates that this method produces a bean to be managed by Spring
     * @return A BCryptPasswordEncoder instance for password hashing and verification
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain that determines how requests are secured.
     *
     * This method defines:
     * - Which URLs are protected and which are publicly accessible
     * - How form login is configured
     * - How logout is handled
     * - Session management behavior
     *
     * URL Authorization Rules:
     * - Public access: /register, CSS/JS resources
     * - Protected access: /inventory (requires authentication)
     * - All other URLs: publicly accessible
     *
     * @Bean - Indicates that this method produces a bean to be managed by Spring
     * @param http - The HttpSecurity object to configure
     * @return The built SecurityFilterChain
     * @throws Exception if there's an error configuring the security filters
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configure URL-based authorization rules
            .authorizeHttpRequests(authorize ->
                authorize
                    // Public resources that don't require authentication
                    .requestMatchers("/register", "/register/**", "/css/**", "/js/**").permitAll()
                    // Default policy for all other requests
                    .anyRequest().permitAll()
            )
            // Configure form-based login
            .formLogin(form ->
                form
                    // Custom login page URL
                    .loginPage("/login")
                    // URL to submit the login form
                    .loginProcessingUrl("/login")
                    // Where to redirect after successful login
                    .defaultSuccessUrl("/home", true)
                    // Allow all users to access the login page
                    .permitAll()
            )
            // Configure logout behavior
            .logout(logout ->
                logout
                    // URL that triggers logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    // Where to redirect after logout
                    .logoutSuccessUrl("/login?logout")
                    // Invalidate the HTTP session
                    .invalidateHttpSession(true)
                    // Remove the session cookie
                    .deleteCookies("JSESSIONID")
                    // Allow all users to logout
                    .permitAll()
            );

        return http.build();
    }

    /**
     * Creates and configures the AuthenticationManager bean.
     *
     * The AuthenticationManager:
     * - Is the main Spring Security interface for authenticating users
     * - Processes authentication requests
     * - Delegates to the configured UserDetailsService and PasswordEncoder
     *
     * In our microservices architecture:
     * - Authentication is handled by this service
     * - User details are retrieved from the Data Access Service
     * - Passwords are verified using BCrypt
     *
     * @Bean - Indicates that this method produces a bean to be managed by Spring
     * @param http - The HttpSecurity object to get the shared AuthenticationManagerBuilder
     * @return A configured AuthenticationManager
     * @throws Exception if there's an error building the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                // Configure which service to use for loading user details
                .userDetailsService(userDetailsService)
                // Configure which encoder to use for verifying passwords
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
