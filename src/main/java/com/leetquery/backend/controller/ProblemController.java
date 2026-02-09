package com.leetquery.backend.controller;

import com.leetquery.backend.model.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProblemController {
    
    private final JdbcTemplate jdbcTemplate;
    
    @GetMapping("/problems/{stageId}")
    public List<Problem> getProblemsByStage(@PathVariable Integer stageId) {
        String sql = "SELECT id, stage_id, title, description, expected_query FROM problems WHERE stage_id = ?";
        
        return jdbcTemplate.query(sql, new Object[]{stageId}, (rs, rowNum) -> {
            Problem problem = new Problem();
            problem.setId(rs.getInt("id"));
            problem.setStageId(rs.getInt("stage_id"));
            problem.setTitle(rs.getString("title"));
            problem.setDescription(rs.getString("description"));
            problem.setExpectedQuery(rs.getString("expected_query"));
            return problem;
        });
    }
    
    @PostMapping("/admin/problem")
    public ResponseEntity<String> createProblem(@RequestBody Problem problem) {
        String sql = "INSERT INTO problems (stage_id, title, description, expected_query) VALUES (?, ?, ?, ?)";
        
        int rowsAffected = jdbcTemplate.update(
            sql,
            problem.getStageId(),
            problem.getTitle(),
            problem.getDescription(),
            problem.getExpectedQuery()
        );
        
        if (rowsAffected > 0) {
            return ResponseEntity.ok("Problem created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create problem");
        }
    }
}
