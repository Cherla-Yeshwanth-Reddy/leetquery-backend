package com.leetquery.backend.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddProblemRequest {
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotNull(message = "Stage order cannot be null")
    @Positive(message = "Stage order must be positive")
    private int stageOrder;
    
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Description cannot be blank")
    @Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
    private String description;
    
    @NotBlank(message = "Expected query cannot be blank")
    @Size(min = 1, max = 10000, message = "Expected query must be between 1 and 10000 characters")
    private String expectedQuery;
}
