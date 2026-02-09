package com.leetquery.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class AddProblemRequest {
    private String password;
    private String title;
    private String description;
    private String difficulty;
    private List<StageRequest> stages;

    @Data
    public static class StageRequest {
        private int stageNumber;
        private String question;
        private String expectedQuery;
    }
}
