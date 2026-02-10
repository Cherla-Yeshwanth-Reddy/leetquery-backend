package com.leetquery.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    
    private Integer weekNumber;
    private Long challengeId;
    
    @NotBlank(message = "Query cannot be empty")
    private String userQuery;
    
    // For compatibility with any existing code that uses .getQuery()
    public String getQuery() {
        return userQuery;
    }
}
