package com.Travellers.DreamRoute.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "DreamRoute");

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                response.put("database", "UP");
            } else {
                response.put("database", "DOWN");
                response.put("status", "DOWN");
            }
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
        }

        if ("DOWN".equals(response.get("status"))) {
            return ResponseEntity.status(503).body(response);
        }

        return ResponseEntity.ok(response);
    }
}