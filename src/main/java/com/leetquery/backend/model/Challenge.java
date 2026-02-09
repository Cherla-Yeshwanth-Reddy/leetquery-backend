package com.leetquery.backend.model;

import lombok.Data;

@Data
public class Challenge {
    private Long id;
    private Integer stageNumber;
    private String stageTitle;
    private String difficulty;
    private String description;
    private String expectedQuery;
    private String hint;
    private String relationalAlgebraHint;
    private String successMessage;
    private String challengeType;
}
