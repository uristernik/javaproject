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
            Integer quantityKG = ((Number) updateData.get("quantityKG")).intValue();
            Integer stockKG = ((Number) updateData.get("stockKG")).intValue();
            
            String sql = "UPDATE inventory SET quantitykg = ?, stockkg = ? WHERE productid = ?";
            int rowsAffected = jdbcTemplate.update(sql, quantityKG, stockKG, productId);
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }
        } else {
            throw new RuntimeException("Updates to table " + tableName + " are not supported");
        }
    }
}
