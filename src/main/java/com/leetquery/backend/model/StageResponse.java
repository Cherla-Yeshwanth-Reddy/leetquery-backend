package com.leetquery.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageResponse {
    private int stageNumber;
    private String stageTitle;
    private List<ProblemResponse> problems;
}
