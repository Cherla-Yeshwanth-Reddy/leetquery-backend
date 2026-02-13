package com.leetquery.backend.dto;

import jakarta.validation.constraints.*;

public class ProblemResponse {
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID must be positive")
    private int id;
    
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Description cannot be blank")
    @Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
    private String description;

    public ProblemResponse(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
