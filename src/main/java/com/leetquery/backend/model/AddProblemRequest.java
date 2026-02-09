package com.leetquery.backend.model;

import lombok.Data;

@Data
public class AddProblemRequest {
    private String password;
    private int stageOrder;
    private String title;
    private String description;
    private String expectedQuery;
}
