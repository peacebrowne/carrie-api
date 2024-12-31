package com.example.carrie.entities;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Comment {

  /*
   * ID of the Comment.
   * This field represents a unique identifier for the comment.
   */
  private String id;

  /*
   * ID of the article that the Comment belongs to.
   * This field links the comment to a specific article by its ID.
   */
  private String articleID;

  /*
   * ID of the Author that post the comment.
   * This field links the comment to a specific author by its ID.
   */
  private String authorID;

  /*
   * ID of the comment that is replied to.
   * This field links the replies to a specific comment by its ID.
   */
  private String parentCommentID;

  /*
   * Actual content of the Comment.
   * The content should be at least 5 characters and no more than 100 characters
   * long.
   * It must not be empty.
   */
  @NotEmpty(message = "Please provide a Comment!")
  @Size(min = 5, max = 100, message = "Comment must be at least 5 characters and no more than 100 characters")
  private String content;

  /*
   * Local date and time the Comment was created.
   * This field records the timestamp when the comment was first created.
   */
  private LocalDateTime createdAt;

  /*
   * Local date and time the Comment was updated.
   * This field stores the timestamp when the comment was last updated.
   */
  private LocalDateTime updatedAt;

}
