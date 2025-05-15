package com.example.adminservice.model;

/**
 * User Model
 *
 * This class represents a user in the system and is used for:
 * - User management in the admin interface
 * - Displaying user information
 * - Creating and editing users
 * - Determining user roles and permissions
 *
 * In our microservices architecture:
 * - This model is used by the Admin Service for user management
 * - It corresponds to the 'users' table in the database
 * - It's populated from data retrieved from the Data Access Service
 * - It's used primarily for the user management functionality
 * - Only administrators can view and modify user information
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
     * Note: For security reasons, this field is typically not populated
     * when retrieving user data for display
     */
    private String password;

    /**
     * User's type/role
     * 1 = Regular user
     * 2 = Admin user
     */
    private Integer type;

    // Getters and setters

    /**
     * Gets the user ID
     * @return The unique identifier for the user
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID
     * @param id The unique identifier for the user
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's first name
     * @return The user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     * @param firstName The user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name
     * @return The user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     * @param lastName The user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's email address
     * @return The user's email address (used as username for login)
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address
     * @param email The user's email address (used as username for login)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number
     * @return The user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number
     * @param phone The user's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the user's password
     * Note: For security reasons, this typically returns a masked or empty value
     * @return The user's password (may be masked)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     * Note: In production, this would typically be hashed before storage
     * @param password The user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's type/role
     * @return The user's type (1=Regular, 2=Admin)
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the user's type/role
     * @param type The user's type (1=Regular, 2=Admin)
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * Helper method to get user type as a human-readable string
     *
     * This method converts the numeric type to a descriptive string:
     * - 1 = "Regular User"
     * - 2 = "Admin"
     * - null or other = "Unknown"
     *
     * @return A string representation of the user type
     */
    public String getTypeAsString() {
        if (type == null) return "Unknown";
        return switch (type) {
            case 1 -> "Regular User";
            case 2 -> "Admin";
            default -> "Unknown";
        };
    }

    /**
     * Helper method to check if the user has admin privileges
     *
     * This method determines if the user is an administrator (type=2)
     * Used for authorization checks in the admin interface
     *
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return type != null && type == 2;
    }
}
