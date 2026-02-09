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
        List<StageResponse> result = new ArrayList<>();

        // Fetch all stages ordered by order_no
        String stageQuery = "SELECT id, title, description, order_no FROM stages ORDER BY order_no";
        List<Map<String, Object>> stages = jdbcTemplate.queryForList(stageQuery);

        for (Map<String, Object> stage : stages) {
            Integer stageId = (Integer) stage.get("id");
            Integer orderNo = (Integer) stage.get("order_no");
            String stageTitle = (String) stage.get("title");

            // Fetch problems for this stage
            String problemQuery = "SELECT title, description, expected_query FROM problems WHERE stage_id = ?";
            List<Map<String, Object>> problemsData = jdbcTemplate.queryForList(problemQuery, stageId);

            // Build problem list
            List<ProblemResponse> problems = new ArrayList<>();
            for (Map<String, Object> problemData : problemsData) {
                ProblemResponse problem = new ProblemResponse(
                    (String) problemData.get("title"),
                    (String) problemData.get("description"),
                    (String) problemData.get("expected_query")
                );
                problems.add(problem);
            }

            // Build stage response
            StageResponse stageResponse = new StageResponse(orderNo, stageTitle, problems);
            result.add(stageResponse);
        }

        return result;
    }
}
