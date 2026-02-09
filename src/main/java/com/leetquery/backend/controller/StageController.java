package com.leetquery.backend.controller;

import com.leetquery.backend.model.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StageController {
    
    private final JdbcTemplate jdbcTemplate;
    
    @GetMapping("/stages")
    public List<Stage> getAllStages() {
        String sql = "SELECT id, title, description, order_no FROM stages ORDER BY order_no";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Stage stage = new Stage();
            stage.setId(rs.getInt("id"));
            stage.setTitle(rs.getString("title"));
            stage.setDescription(rs.getString("description"));
            stage.setOrderNo(rs.getInt("order_no"));
            return stage;
        });
    }
}
