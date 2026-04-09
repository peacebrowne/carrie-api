package com.example.carrie.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Article {

  private String id;

  @NotEmpty(message = "Please provide a title for the Article!")
  @Size(min = 5, max = 150, message = "Article title must be between 5 and 150 characters.")
  private String title;

  private String authorID;

  private String content;

  private String description;

  @NotEmpty(message = "Invalid Article status.")
  @Pattern(regexp = "DRAFT|SCHEDULED|PUBLISHED",
          message = "Status must be uppercase: DRAFT, SCHEDULED, PUBLISHED")
  private String status;

  private boolean isTrash;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime publishDate;

  /* * List of associated Tag names or IDs
   */
  private List<String> tags;

  // --- Optimized Counter Columns (Mapped from DB Triggers) ---

  private Long totalLikes = 0L;

  private Long totalDislikes = 0L;

  private Long totalComments = 0L;

  private Long totalViews = 0L;

  private Long totalReads = 0L;

  private Long authorFollowerCount = 0L;
}