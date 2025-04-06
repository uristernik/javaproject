package com.example.dataaccessservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            Integer stockKG = ((Number) updateData.get("stockKG")).intValue();
            
            String sql = "UPDATE inventory SET stockkg = ? WHERE productid = ?";
            int rowsAffected = jdbcTemplate.update(sql, stockKG, productId);
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Product not found with ID: " + productId);
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

        // Insert into ORDERS table
        String orderSql = "INSERT INTO ORDERS (userID, deliveryAddress, totalPrice) VALUES (?, ?, ?) RETURNING orderID";
        Long orderId = jdbcTemplate.queryForObject(orderSql, Long.class,
            userId,
            deliveryAddress,
            ((Number) orderData.get("totalPrice")).intValue());

        // Insert order items
        String itemsSql = "INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG) VALUES (?, ?, ?, ?)";
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        
        for (Map<String, Object> item : items) {
            jdbcTemplate.update(itemsSql,
                orderId,
                ((Number) item.get("productId")).longValue(),
                ((Number) item.get("quantity")).intValue(),
                ((Number) item.get("pricePerKG")).intValue());
        }

        return orderId;
    }

    @Transactional
    public void updateInventoryBatch(List<Map<String, Object>> updates) {
        String sql = "UPDATE inventory SET stockkg = stockkg - ? WHERE productid = ? AND stockkg >= ?";
        
        for (Map<String, Object> update : updates) {
            int rowsAffected = jdbcTemplate.update(sql,
                update.get("quantity"),
                update.get("productId"),
                update.get("quantity"));
                
            if (rowsAffected == 0) {
                throw new RuntimeException("Insufficient stock for product ID: " + update.get("productId"));
            }
        }
    }
}
