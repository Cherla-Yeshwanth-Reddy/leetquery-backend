package com.leetquery.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    
    private boolean success;
    private String queryType;
    private List<String> headers;
    private List<List<String>> rows;
    private Integer rowCount;
    private String message;
}
