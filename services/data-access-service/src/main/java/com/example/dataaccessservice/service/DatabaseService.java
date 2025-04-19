package com.example.dataaccessservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getTableData(String tableName) {
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.queryForList(sql);
    }

    public List<String> getAllTables() {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Transactional
    public void updateTableData(String tableName, Map<String, Object> updateData) {
        if ("inventory".equalsIgnoreCase(tableName)) {
            Long productId = ((Number) updateData.get("productId")).longValue();

            if (updateData.containsKey("stockKG")) {
                Double stockKG = ((Number) updateData.get("stockKG")).doubleValue();
                String sql = "UPDATE inventory SET stockkg = ? WHERE productid = ?";
                int rowsAffected = jdbcTemplate.update(sql, stockKG, productId);

                if (rowsAffected == 0) {
                    throw new RuntimeException("Product not found with ID: " + productId);
                }
            }

            if (updateData.containsKey("pricePerKG")) {
                Integer pricePerKG = ((Number) updateData.get("pricePerKG")).intValue();
                String sql = "UPDATE inventory SET priceperkg = ? WHERE productid = ?";
                int rowsAffected = jdbcTemplate.update(sql, pricePerKG, productId);

                if (rowsAffected == 0) {
                    throw new RuntimeException("Product not found with ID: " + productId);
                }
            }
        } else {
            throw new RuntimeException("Updates to table " + tableName + " are not supported");
        }
    }

    @Transactional
    public Long createOrder(Map<String, Object> orderData) {
        // Verify user exists
        Long userId = ((Number) orderData.get("userId")).longValue();
        String checkUserSql = "SELECT COUNT(*) FROM USERS WHERE userId = ?";
        int userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);
        if (userCount == 0) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Get the delivery address as string
        String deliveryAddress = orderData.get("deliveryAddress").toString();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("deliveryAddress is required");
        }

        // Calculate the next userOrderId for this user
        String userOrderIdSql = "SELECT COALESCE(MAX(userOrderId), 0) + 1 FROM ORDERS WHERE userId = ?";
        Integer userOrderId = jdbcTemplate.queryForObject(userOrderIdSql, Integer.class, userId);

        // Insert into ORDERS table
        String orderSql = "INSERT INTO ORDERS (userID, userOrderId, deliveryAddress, totalPrice) VALUES (?, ?, ?, ?) RETURNING orderID";

        // Keep totalPrice as double to preserve decimal values
        Number totalPriceObj = (Number) orderData.get("totalPrice");
        double totalPrice = totalPriceObj.doubleValue();

        Long orderId = jdbcTemplate.queryForObject(orderSql, Long.class,
            userId,
            userOrderId,
            deliveryAddress,
            totalPrice);

        // Insert order items
        String itemsSql = "INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG) VALUES (?, ?, ?, ?)";
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");

        for (Map<String, Object> item : items) {
            // Keep quantity as double to preserve decimal values
            double quantityDouble = ((Number) item.get("quantity")).doubleValue();

            // Convert price from double to int if needed
            Number priceObj = (Number) item.get("pricePerKG");
            int priceInt;
            if (priceObj instanceof Double || priceObj instanceof Float) {
                priceInt = (int) Math.round(priceObj.doubleValue());
            } else {
                priceInt = priceObj.intValue();
            }

            jdbcTemplate.update(itemsSql,
                orderId,
                ((Number) item.get("productId")).longValue(),
                quantityDouble,
                priceInt);
        }

        return orderId;
    }

    @Transactional
    public void updateInventoryBatch(List<Map<String, Object>> updates) {
        String sql = "UPDATE inventory SET stockkg = stockkg - ? WHERE productid = ? AND stockkg >= ?";

        for (Map<String, Object> update : updates) {
            // Get quantity as double to preserve decimal values
            Number quantityObj = (Number) update.get("quantity");
            double quantityValue = quantityObj.doubleValue();

            int rowsAffected = jdbcTemplate.update(sql,
                quantityValue,
                update.get("productId"),
                quantityValue);

            if (rowsAffected == 0) {
                throw new RuntimeException("Insufficient stock for product ID: " + update.get("productId"));
            }
        }
    }

    public List<Map<String, Object>> getUserOrders(Long userId) {
        String sql = """
            SELECT
                o.orderid,
                o.userorderid,
                o.deliveryaddress,
                o.totalprice,
                json_agg(
                    json_build_object(
                        'description', i.description,
                        'quantitykg', oi.quantitykg,
                        'pricepkg', oi.priceperkg
                    )
                ) as items
            FROM orders o
            JOIN order_items oi ON o.orderid = oi.orderid
            JOIN inventory i ON oi.productid = i.productid
            WHERE o.userid = ?
            GROUP BY o.orderid, o.userorderid, o.deliveryaddress, o.totalprice
            ORDER BY o.orderid DESC
        """;

        return jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Map<String, Object> order = new HashMap<>();
                order.put("orderid", rs.getLong("orderid"));
                order.put("userorderid", rs.getInt("userorderid"));
                order.put("deliveryaddress", rs.getString("deliveryaddress"));
                order.put("totalprice", rs.getDouble("totalprice"));

                // Parse the JSON string into a List
                String itemsJson = rs.getString("items");
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<?> items = mapper.readValue(itemsJson, List.class);
                    order.put("items", items);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing order items JSON", e);
                }

                return order;
            },
            userId
        );
    }

    // Method to get all orders (for admin)
    public List<Map<String, Object>> getAllOrders() {
        String sql = """
            SELECT
                o.orderid,
                o.userorderid,
                o.userid,
                u.firstname,
                u.lastname,
                o.deliveryaddress,
                o.totalprice,
                json_agg(
                    json_build_object(
                        'description', i.description,
                        'quantitykg', oi.quantitykg,
                        'pricepkg', oi.priceperkg
                    )
                ) as items
            FROM orders o
            JOIN users u ON o.userid = u.userid
            JOIN order_items oi ON o.orderid = oi.orderid
            JOIN inventory i ON oi.productid = i.productid
            GROUP BY o.orderid, o.userorderid, o.userid, u.firstname, u.lastname, o.deliveryaddress, o.totalprice
            ORDER BY o.orderid DESC
        """;

        return jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Map<String, Object> order = new HashMap<>();
                order.put("orderid", rs.getLong("orderid"));
                order.put("userorderid", rs.getInt("userorderid"));
                order.put("userid", rs.getLong("userid"));
                order.put("username", rs.getString("firstname") + " " + rs.getString("lastname"));
                order.put("deliveryaddress", rs.getString("deliveryaddress"));
                order.put("totalprice", rs.getDouble("totalprice"));

                // Parse the JSON string into a List
                String itemsJson = rs.getString("items");
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<?> items = mapper.readValue(itemsJson, List.class);
                    order.put("items", items);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing order items JSON", e);
                }

                return order;
            }
        );
    }

    // User management methods

    public List<Map<String, Object>> getAllUsers() {
        String sql = "SELECT userid, firstname, lastname, email, phone, type FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public Map<String, Object> getUserByEmail(String email) {
        System.out.println("DatabaseService: Getting user by email: " + email);
        String sql = "SELECT userid, firstname, lastname, email, phone, type, hashedpassword FROM users WHERE email = ?";

        try {
            List<Map<String, Object>> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getLong("userid"));
                user.put("firstName", rs.getString("firstname"));
                user.put("lastName", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("phone", rs.getString("phone"));
                user.put("type", rs.getInt("type"));
                // Store the hashed password with both keys for compatibility
                String hashedPassword = rs.getString("hashedpassword");
                user.put("password", hashedPassword);
                user.put("hashedPassword", hashedPassword);
                return user;
            }, email);

            if (users.isEmpty()) {
                System.out.println("DatabaseService: No user found with email: " + email);
                return null;
            }

            Map<String, Object> user = users.get(0);
            System.out.println("DatabaseService: Found user: " + user);
            System.out.println("DatabaseService: Password in DB: " + user.get("password"));
            return user;
        } catch (Exception e) {
            System.err.println("DatabaseService: Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Transactional
    public Map<String, Object> registerNewUser(Map<String, Object> userData) {
        System.out.println("DatabaseService: Registering new user with data: " + userData);

        // Check if user already exists
        String email = (String) userData.get("email");
        if (existsByEmail(email)) {
            System.out.println("DatabaseService: Email already in use: " + email);
            throw new RuntimeException("Email already in use");
        }

        // Hash the password
        String password = (String) userData.get("password");
        System.out.println("DatabaseService: Hashing password for user: " + email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        // Set default user type (1 for regular user)
        int userType = 1;

        try {
            // Insert the new user
            String sql = "INSERT INTO users (firstname, lastname, email, phone, hashedpassword, type) VALUES (?, ?, ?, ?, ?, ?) RETURNING userid";
            System.out.println("DatabaseService: Executing SQL to insert new user");
            Long userId = jdbcTemplate.queryForObject(sql, Long.class,
                    userData.get("firstName"),
                    userData.get("lastName"),
                    email,
                    userData.get("phone"),
                    hashedPassword,
                    userType);

            System.out.println("DatabaseService: User inserted with ID: " + userId);

            // Return the created user
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("id", userId);
            newUser.put("firstName", userData.get("firstName"));
            newUser.put("lastName", userData.get("lastName"));
            newUser.put("email", email);
            newUser.put("phone", userData.get("phone"));
            newUser.put("type", userType);

            System.out.println("DatabaseService: Returning new user data: " + newUser);
            return newUser;
        } catch (Exception e) {
            System.err.println("DatabaseService: Error registering new user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        // First check if user exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE userid = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId);
        if (count == 0) {
            return false;
        }

        // Check if user has orders
        String checkOrdersSql = "SELECT COUNT(*) FROM orders WHERE userid = ?";
        int orderCount = jdbcTemplate.queryForObject(checkOrdersSql, Integer.class, userId);
        if (orderCount > 0) {
            // Delete associated order items first
            String deleteOrderItemsSql = "DELETE FROM order_items WHERE orderid IN (SELECT orderid FROM orders WHERE userid = ?)";
            jdbcTemplate.update(deleteOrderItemsSql, userId);

            // Then delete orders
            String deleteOrdersSql = "DELETE FROM orders WHERE userid = ?";
            jdbcTemplate.update(deleteOrdersSql, userId);
        }

        // Finally delete the user
        String deleteUserSql = "DELETE FROM users WHERE userid = ?";
        int rowsAffected = jdbcTemplate.update(deleteUserSql, userId);
        return rowsAffected > 0;
    }

    @Transactional
    public boolean resetUserPassword(Long userId, String newPassword) {
        // Check if user exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE userid = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId);
        if (count == 0) {
            return false;
        }

        // Hash the new password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update the password
        String updateSql = "UPDATE users SET hashedpassword = ? WHERE userid = ?";
        int rowsAffected = jdbcTemplate.update(updateSql, hashedPassword, userId);
        return rowsAffected > 0;
    }

    // User row mapper
    private static class UserRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> user = new HashMap<>();
            user.put("id", rs.getLong("userid"));
            user.put("firstName", rs.getString("firstname"));
            user.put("lastName", rs.getString("lastname"));
            user.put("email", rs.getString("email"));
            user.put("phone", rs.getString("phone"));
            user.put("type", rs.getInt("type"));
            return user;
        }
    }
}
