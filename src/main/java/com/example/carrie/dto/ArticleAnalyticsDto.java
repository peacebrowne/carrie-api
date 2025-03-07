package com.example.carrie.dto;

import lombok.Data;

@Data
public class ArticleAnalyticsDto {
    private Long total;

    private Long pending;

    private Long published;

    private Long draft;

    private Long likes;

    private Long dislikes;


    public ArticleAnalyticsDto(Long total, Long pending, Long published, Long draft, Long likes, Long dislikes) {
        this.total = total;
        this.pending = pending;
        this.published = published;
        this.draft = draft;
        this.likes = likes;
        this.dislikes = dislikes;
    }
}
