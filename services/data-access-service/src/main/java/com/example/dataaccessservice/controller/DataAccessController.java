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
}
