package com.leetquery.backend.controller;

import com.leetquery.backend.model.ProblemResponse;
import com.leetquery.backend.model.StageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ProblemsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/problems")
    public List<StageResponse> getProblems() {
        String query = "SELECT s.order_no, s.title AS stage_title, p.id, p.title AS problem_title, p.description " +
                       "FROM stages s " +
                       "JOIN problems p ON p.stage_id = s.id " +
                       "ORDER BY s.order_no";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        // Group problems by stage
        Map<Integer, StageResponse> stageMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Integer stageNumber = (Integer) row.get("order_no");
            String stageTitle = (String) row.get("stage_title");
            Integer problemId = (Integer) row.get("id");
            String problemTitle = (String) row.get("problem_title");
            String problemDescription = (String) row.get("description");

            // Get or create stage response
            StageResponse stageResponse = stageMap.get(stageNumber);
            if (stageResponse == null) {
                stageResponse = new StageResponse(stageNumber, stageTitle, new ArrayList<>());
                stageMap.put(stageNumber, stageResponse);
            }

            // Add problem to stage
            ProblemResponse problem = new ProblemResponse(problemId, problemTitle, problemDescription);
            stageResponse.getProblems().add(problem);
        }

        return new ArrayList<>(stageMap.values());
    }
}
