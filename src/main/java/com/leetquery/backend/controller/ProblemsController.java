package com.leetquery.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ProblemsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/problems")
    public ResponseEntity<?> getProblems() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();

            // Fetch all stages ordered by order_no
            String stageQuery = "SELECT id, title, description, order_no FROM stages ORDER BY order_no";
            List<Map<String, Object>> stages = jdbcTemplate.queryForList(stageQuery);

            for (Map<String, Object> stage : stages) {
                Integer stageId = (Integer) stage.get("id");
                Integer orderNo = (Integer) stage.get("order_no");
                String stageTitle = (String) stage.get("title");

                // Fetch problems for this stage
                String problemQuery = "SELECT title, description, expected_query FROM problems WHERE stage_id = ?";
                List<Map<String, Object>> problems = jdbcTemplate.queryForList(problemQuery, stageId);

                // Build response object for this stage
                Map<String, Object> stageData = new HashMap<>();
                stageData.put("stageNumber", orderNo);
                stageData.put("stageTitle", stageTitle);
                
                // Transform problems to match expected structure
                List<Map<String, Object>> problemList = new ArrayList<>();
                for (Map<String, Object> problem : problems) {
                    Map<String, Object> problemData = new HashMap<>();
                    problemData.put("title", problem.get("title"));
                    problemData.put("description", problem.get("description"));
                    problemData.put("expectedQuery", problem.get("expected_query"));
                    problemList.add(problemData);
                }
                
                stageData.put("problems", problemList);
                result.add(stageData);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
