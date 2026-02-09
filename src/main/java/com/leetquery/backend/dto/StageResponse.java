package com.leetquery.backend.dto;

import java.util.List;

public class StageResponse {
    private int stageNumber;
    private String stageTitle;
    private List<ProblemResponse> problems;

    public StageResponse(int stageNumber, String stageTitle, List<ProblemResponse> problems) {
        this.stageNumber = stageNumber;
        this.stageTitle = stageTitle;
        this.problems = problems;
    }

    public int getStageNumber() { return stageNumber; }
    public String getStageTitle() { return stageTitle; }
    public List<ProblemResponse> getProblems() { return problems; }
}
