package com.example.adminservice.service;

import com.example.adminservice.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserManagementService {

    private final WebClient webClient;

    public UserManagementService() {
        this.webClient = WebClient.create("http://data-access-service:8085");
    }

    public List<User> getAllUsers() {
        List<Map<String, Object>> userMaps = webClient.get()
                .uri("/api/data/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        if (userMaps == null) {
            return List.of();
        }

        return userMaps.stream()
                .map(this::convertToUser)
                .toList();
    }

    private User convertToUser(Map<String, Object> userMap) {
        User user = new User();
        if (userMap.containsKey("id")) {
            user.setId(((Number) userMap.get("id")).longValue());
        }
        if (userMap.containsKey("firstName")) {
            user.setFirstName((String) userMap.get("firstName"));
        }
        if (userMap.containsKey("lastName")) {
            user.setLastName((String) userMap.get("lastName"));
        }
        if (userMap.containsKey("email")) {
            user.setEmail((String) userMap.get("email"));
        }
        if (userMap.containsKey("phone")) {
            user.setPhone((String) userMap.get("phone"));
        }
        if (userMap.containsKey("type")) {
            user.setType(((Number) userMap.get("type")).intValue());
        }
        return user;
    }

    public boolean deleteUser(Long userId) {
        return webClient.delete()
                .uri("/api/data/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public boolean resetPassword(Long userId, String newPassword) {
        Map<String, Object> passwordData = new HashMap<>();
        passwordData.put("userId", userId);
        passwordData.put("newPassword", newPassword);

        return webClient.post()
                .uri("/api/data/users/reset-password")
                .bodyValue(passwordData)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
