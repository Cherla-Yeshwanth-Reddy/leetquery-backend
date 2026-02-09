package com.leetquery.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    private Integer id;
    private Integer stageId;
    private String title;
    private String description;
    private String expectedQuery;
}
