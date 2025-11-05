package com.example.carrie.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadingList {
    private String id;
    private String authorId;
    private String articleId;
    private LocalDateTime savedAt;
}
