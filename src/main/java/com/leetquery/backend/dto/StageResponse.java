package com.leetquery.backend.dto;

import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

public class StageResponse {
    @NotNull(message = "Stage number cannot be null")
    @Positive(message = "Stage number must be positive")
    private int stageNumber;
    
    @NotBlank(message = "Stage title cannot be blank")
    @Size(min = 1, max = 255, message = "Stage title must be between 1 and 255 characters")
    private String stageTitle;
    
    @NotNull(message = "Problems list cannot be null")
    @Size(min = 0, max = 1000, message = "Stage cannot have more than 1000 problems")
    @Valid
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
