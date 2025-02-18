package com.example.carrie.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Clap {

  /**
   * Unique identifier for the Clap.
   */
  private String id;

  /**
   * ID of the author who clapped for the article or comment.
   * This field links the clap to a specific author by their ID.
   */
  private String authorID;

  /**
   * ID of the article that received the clap.
   * This field links the clap to a specific article by its ID.
   */
  private String articleID;

  /**
   * ID of the comment that received the clap.
   * This field links the clap to a specific comment by its ID.
   */
  private String commentID;

  /**
   * Total count of claps given by the author.
   */
  private Long count;

  /**
   * Timestamp indicating when the clap was created.
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp indicating when the clap was last updated.
   */
  private LocalDateTime updatedAt;

}
