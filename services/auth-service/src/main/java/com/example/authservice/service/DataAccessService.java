package com.example.authservice.service;

import com.example.authservice.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DataAccessService {
    private final WebClient webClient;

    public DataAccessService() {
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    public Optional<User> findUserByEmail(String email) {
        System.out.println("DataAccessService: Finding user by email: " + email);
        try {
            System.out.println("Making request to: http://data-access-service:8085/api/data/users/email/" + email);
            Map<String, Object> userData = webClient.get()
                    .uri("/api/data/users/email/{email}", email)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (userData == null) {
                System.out.println("DataAccessService: No user data returned for email: " + email);
                return Optional.empty();
            }

            System.out.println("DataAccessService: User data received: " + userData);

            User user = new User();
            user.setId(((Number) userData.get("id")).longValue());
            user.setFirstName((String) userData.get("firstName"));
            user.setLastName((String) userData.get("lastName"));
            user.setEmail((String) userData.get("email"));
            user.setPhone((String) userData.get("phone"));

            // The password field might be coming with a different name from the database
            // Try both "password" and "hashedPassword" keys
            String password = (String) userData.get("password");
            if (password == null || password.isEmpty()) {
                System.out.println("Password not found under 'password' key, trying 'hashedPassword'");
                password = (String) userData.get("hashedPassword");
            }

            user.setPassword(password);
            user.setType(((Number) userData.get("type")).intValue());

            System.out.println("DataAccessService: User object created with email: " + user.getEmail());
            System.out.println("DataAccessService: Password from DB: " + user.getPassword());

            return Optional.of(user);
        } catch (Exception e) {
            System.err.println("DataAccessService: Error finding user by email: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        try {
            Boolean exists = webClient.get()
                    .uri("/api/data/users/exists/{email}", email)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return exists != null && exists;
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error checking if email exists: " + e.getMessage());
            return false;
        }
    }

    public User registerNewUser(User user) {
        System.out.println("DataAccessService: Registering new user with email: " + user.getEmail());

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("password", user.getPassword());

        System.out.println("DataAccessService: Sending registration data to data-access-service");

        try {
            Map<String, Object> newUserData = webClient.post()
                    .uri("/api/data/users/register")
                    .bodyValue(userData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (newUserData == null) {
                System.err.println("DataAccessService: Received null response from data-access-service");
                throw new RuntimeException("Failed to register user");
            }

            System.out.println("DataAccessService: Registration successful, received user data: " + newUserData);

            User newUser = new User();
            newUser.setId(((Number) newUserData.get("id")).longValue());
            newUser.setFirstName((String) newUserData.get("firstName"));
            newUser.setLastName((String) newUserData.get("lastName"));
            newUser.setEmail((String) newUserData.get("email"));
            newUser.setPhone((String) newUserData.get("phone"));
            newUser.setType(((Number) newUserData.get("type")).intValue());

            System.out.println("DataAccessService: Created new user object with ID: " + newUser.getId());
            return newUser;
        } catch (Exception e) {
            System.err.println("DataAccessService: Error registering new user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }
}
