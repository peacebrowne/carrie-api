package com.example.carrie.dto;

import lombok.Data;

@Data
public class DailyStatsDto {
    private String label;
    private Long claps;
    private Long reads;
    private Long views;
}
