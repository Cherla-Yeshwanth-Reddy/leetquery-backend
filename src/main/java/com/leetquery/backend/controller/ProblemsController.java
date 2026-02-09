package com.leetquery.backend.controller;

import com.leetquery.backend.dto.ProblemResponse;
import com.leetquery.backend.dto.StageResponse;
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
    public List<StageResponse> getAllStagesWithProblems() {

        List<Map<String, Object>> stages = jdbcTemplate.queryForList(
            "SELECT id, title, order_no FROM stages ORDER BY order_no"
        );

        List<StageResponse> result = new ArrayList<>();

        for (Map<String, Object> stage : stages) {

            int stageId = (int) stage.get("id");
            int stageNumber = (int) stage.get("order_no");
            String stageTitle = (String) stage.get("title");

            List<Map<String, Object>> problems = jdbcTemplate.queryForList(
                "SELECT id, title, description FROM problems WHERE stage_id = ?",
                stageId
            );

            List<ProblemResponse> problemList = problems.stream()
                .map(p -> new ProblemResponse(
                    (int) p.get("id"),
                    (String) p.get("title"),
                    (String) p.get("description")
                ))
                .toList();

            result.add(new StageResponse(stageNumber, stageTitle, problemList));
        }

        return result;
    }
}
