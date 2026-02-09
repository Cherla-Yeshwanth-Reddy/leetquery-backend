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
            // Insert problem and get generated ID
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO problems (title, description, difficulty) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, request.getTitle());
                ps.setString(2, request.getDescription());
                ps.setString(3, request.getDifficulty());
                return ps;
            }, keyHolder);

            int problemId = keyHolder.getKey().intValue();

            // Insert stages
            if (request.getStages() != null && !request.getStages().isEmpty()) {
                for (AddProblemRequest.StageRequest stage : request.getStages()) {
                    jdbcTemplate.update(
                        "INSERT INTO stages (problem_id, stage_number, question, expected_query) VALUES (?, ?, ?, ?)",
                        problemId,
                        stage.getStageNumber(),
                        stage.getQuestion(),
                        stage.getExpectedQuery()
                    );
                }
            }

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("problemId", problemId);
            
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
