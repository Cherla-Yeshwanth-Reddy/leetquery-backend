package com.leetquery.backend.model;

import lombok.Data;

@Data
public class SchemaResponse {
    private String schemaInfo;
    
    public SchemaResponse(String schemaInfo) {
        this.schemaInfo = schemaInfo;
    }
}
