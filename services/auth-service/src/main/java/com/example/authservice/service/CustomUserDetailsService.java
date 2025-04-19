package com.example.authservice.service;

import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DataAccessService dataAccessService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting to load user by email: " + email);

        try {
            Optional<User> userOpt = dataAccessService.findUserByEmail(email);

            if (userOpt.isEmpty()) {
                System.out.println("User not found with email: " + email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            User user = userOpt.get();
            System.out.println("User found: " + user.getEmail());
            System.out.println("Password present: " + (user.getPassword() != null && !user.getPassword().isEmpty()));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (Exception e) {
            System.out.println("Error in loadUserByUsername: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage(), e);
        }
    }
}
