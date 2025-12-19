package com.example.carrie.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadingHistory {

    /*
     * ID of the tag.
     * This field represents a unique identifier for the tag.
     */
    private String id;

    /*
     * ID of the tag.
     * This field represents a unique identifier for the tag.
     */
    private String userId;

    /*
     * ID of the tag.
     * This field represents a unique identifier for the tag.
     */
    private String articleId;

    /*
     * ID of the tag.
     * This field represents a unique identifier for the tag.
     */
    private LocalDateTime readAt;

    /*
     * ID of the tag.
     * This field represents a unique identifier for the tag.
     */
    private Integer timeSpentSeconds;
}
