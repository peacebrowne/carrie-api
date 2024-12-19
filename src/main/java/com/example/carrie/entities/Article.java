package com.example.carrie.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class Article {
  private String id;

  @NotEmpty(message = "Please provide a title for the Article!")
  @Size(min = 5, max = 150, message = "An article title should be 5 characters or less than 150 characters")
  private String title;
  private String authorId;

  private String content;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
