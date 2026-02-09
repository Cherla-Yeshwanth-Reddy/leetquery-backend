package com.leetquery.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    private Integer id;
    private String title;
    private String description;
    private Integer orderNo;
}
