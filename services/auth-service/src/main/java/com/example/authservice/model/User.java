package com.example.authservice.model;

/**
 * User Model
 *
 * This class represents a user in the system and is used for:
 * - Authentication and authorization
 * - User profile information
 * - User type/role determination
 *
 * In our microservices architecture:
 * - This model is used by the Auth Service for authentication
 * - It corresponds to the 'users' table in the database
 * - It's populated from data retrieved from the Data Access Service
 * - It's used by Spring Security for authentication
 *
 * User Types:
 * - Type 1: Regular user (customer)
 * - Type 2: Admin user (has administrative privileges)
 */
public class User {
    /**
     * Unique identifier for the user
     */
    private Long id;

    /**
     * User's first name
     */
    private String firstName;

    /**
     * User's last name
     */
    private String lastName;

    /**
     * User's email address (used as username for login)
     */
    private String email;

    /**
     * User's phone number
     */
    private String phone;

    /**
     * User's password (stored in hashed form in the database)
     */
    private String password;

    /**
     * User's type/role
     * 1 = Regular user
     * 2 = Admin user
     */
    private Integer type;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
