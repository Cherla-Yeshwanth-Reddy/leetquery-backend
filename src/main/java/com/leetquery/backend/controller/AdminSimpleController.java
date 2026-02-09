package com.leetquery.backend.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminSimpleController {

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

    @Data
    public static class PasswordRequest {
        private String password;
    }
}
