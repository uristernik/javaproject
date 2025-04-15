package com.example.dataaccessservice.controller;

import com.example.dataaccessservice.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataAccessController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/tables")
    public ResponseEntity<List<String>> getAllTables() {
        return ResponseEntity.ok(databaseService.getAllTables());
    }

    @GetMapping("/tables/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return ResponseEntity.ok(databaseService.getTableData(tableName));
    }

    @PostMapping("/tables/{tableName}/update")
    public ResponseEntity<Void> updateTableData(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> updateData) {
        databaseService.updateTableData(tableName, updateData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        Long orderId = databaseService.createOrder(orderData);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    @PostMapping("/inventory/update-batch")
    public ResponseEntity<Void> updateInventoryBatch(@RequestBody List<Map<String, Object>> updates) {
        databaseService.updateInventoryBatch(updates);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(databaseService.getUserOrders(userId));
    }

    @GetMapping("/orders/all")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        return ResponseEntity.ok(databaseService.getAllOrders());
    }

    // User management endpoints

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(databaseService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(databaseService.deleteUser(userId));
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<Boolean> resetUserPassword(@RequestBody Map<String, Object> passwordData) {
        Long userId = ((Number) passwordData.get("userId")).longValue();
        String newPassword = (String) passwordData.get("newPassword");
        return ResponseEntity.ok(databaseService.resetUserPassword(userId, newPassword));
    }
}
