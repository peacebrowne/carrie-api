package com.example.carrie.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Article {
  private String id;

  @NotEmpty(message = "Please provide a title for the Article!")
  @Size(min = 5, max = 150, message = "Article title must be at least 5 characters and no more than 100 characters.")
  private String title;
  private String authorID;

  private String content;

  private LocalDateTime created_at;

  private LocalDateTime updated_at;

  @Size(min = 5, max = 150, message = "Article description must be at least 5 characters and no more than 150 characters.")
  private String description;

  private Boolean is_published;

  private List<String> tags;

}
