package com.leetquery.backend.controller;

import com.leetquery.backend.model.AddProblemRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminSimpleController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(@RequestBody PasswordRequest request) {
        Map<String, Boolean> response = new HashMap<>();
        
        if ("siva_yesh".equals(request.getPassword())) {
            response.put("valid", true);
        } else {
            response.put("valid", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addProblem")
    public ResponseEntity<?> addProblem(@RequestBody AddProblemRequest request) {
        // Check password
        if (!"siva_yesh".equals(request.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            // Query stage ID by order_no
            Integer stageId = jdbcTemplate.queryForObject(
                "SELECT id FROM stages WHERE order_no = ?",
                Integer.class,
                request.getStageOrder()
            );

            if (stageId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Stage not found for order_no: " + request.getStageOrder());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Insert problem
            jdbcTemplate.update(
                "INSERT INTO problems (stage_id, title, description, expected_query) VALUES (?, ?, ?, ?)",
                stageId,
                request.getTitle(),
                request.getDescription(),
                request.getExpectedQuery()
            );

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Data
    public static class PasswordRequest {
        private String password;
    }
}
