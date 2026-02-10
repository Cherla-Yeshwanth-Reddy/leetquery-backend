package com.leetquery.backend.controller;

import com.leetquery.backend.model.Challenge;
import com.leetquery.backend.model.SchemaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/levels")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LevelController {
    
    private final JdbcTemplate jdbcTemplate;
    
    @GetMapping("/{levelId}/challenges")
    public List<Challenge> getChallenges(@PathVariable int levelId) {
        String sql = "SELECT id, stage_number, stage_title, difficulty, description, " +
                    "expected_query, hint, relational_algebra_hint, success_message, challenge_type " +
                    "FROM challenges WHERE level_id = ? ORDER BY stage_number, " +
                    "CASE difficulty WHEN 'EASY' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'HARD' THEN 3 END";
        
        return jdbcTemplate.query(sql, new Object[]{levelId}, (rs, rowNum) -> {
            Challenge challenge = new Challenge();
            challenge.setId(rs.getLong("id"));
            challenge.setStageNumber(rs.getInt("stage_number"));
            challenge.setStageTitle(rs.getString("stage_title"));
            challenge.setDifficulty(rs.getString("difficulty"));
            challenge.setDescription(rs.getString("description"));
            challenge.setExpectedQuery(rs.getString("expected_query"));
            challenge.setHint(rs.getString("hint"));
            challenge.setRelationalAlgebraHint(rs.getString("relational_algebra_hint"));
            challenge.setSuccessMessage(rs.getString("success_message"));
            challenge.setChallengeType(rs.getString("challenge_type"));
            return challenge;
        });
    }
    
    @GetMapping("/{levelId}/schema")
    public SchemaResponse getSchema(@PathVariable int levelId) {
        String sql = "SELECT schema_info FROM tutorial_schema WHERE level_id = ? LIMIT 1";
        try {
            String schemaInfo = jdbcTemplate.queryForObject(sql, new Object[]{levelId}, String.class);
            return new SchemaResponse(schemaInfo != null ? schemaInfo : "No schema available");
        } catch (Exception e) {
            log.error("Error fetching schema for level {}: {}", levelId, e.getMessage(), e);
            // Handle table missing or data missing gracefully
            return new SchemaResponse("Schema information currently unavailable for level " + levelId + ". Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset")
    public ResponseEntity<Void> resetProgress() {
        // Reset will be handled on client side (clear local progress)
        // Could also clear user progress from database if needed
        return ResponseEntity.ok().build();
    }
}
