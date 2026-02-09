package com.leetquery.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Base64;
import org.json.JSONObject;

@RestController
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/admin/checkRole")
    @ResponseBody
    public String checkRole(@RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String[] parts = token.split("\\.");

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JSONObject json = new JSONObject(payloadJson);

            String userId = json.getString("sub");

            String role = jdbcTemplate.query(
                "SELECT role FROM user_roles WHERE user_id = ?",
                rs -> rs.next() ? rs.getString("role") : "USER",
                userId
            );

            return role != null ? role : "USER";

        } catch (Exception e) {
            return "USER";
        }
    }
}
