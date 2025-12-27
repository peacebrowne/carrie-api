package com.example.carrie.dto;

import lombok.Data;

@Data
public class AnalyticsDto {
    private int views;
    private int reads;
    private int avgReadTime;
    private int claps;

    public AnalyticsDto(int views, int reads, int avgReadTime, int claps) {
        this.views = views;
        this.reads = reads;
        this.avgReadTime = avgReadTime;
        this.claps = claps;
    }
}
