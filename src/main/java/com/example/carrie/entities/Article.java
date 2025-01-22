package com.example.carrie.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Article {
  /*
   * ID of the Article
   */
  private String id;

  /*
   * Title of the Article, and it should be minimum 5 or maximum 150 character and
   * should not be empty.
   */
  @NotEmpty(message = "Please provide a title for the Article!")
  @Size(min = 5, max = 150, message = "Article title must be at least 5 characters and no more than 100 characters.")
  private String title;

  /*
   * ID of the Author that the Article belongs to.
   * This field links the article to a specific author by its ID.
   */
  private String authorID;

  /*
   * Actual content of the Article.
   * This field stores the main text or body of the article.
   */
  private String content;

  /*
   * Local date and time the Article was created.
   * This field records the timestamp when the article was first created.
   */
  private LocalDateTime createdAt;

  /*
   * Local date and time the Article was updated.
   * This field stores the timestamp when the article was last updated.
   */
  private LocalDateTime updatedAt;

  /*
   * Description of the Article.
   * The description should be at least 5 characters and no more than 100
   * characters long.
   * It must not be empty.
   */
  // @Size(min = 5, max = 100, message = "Article description must be at least 5
  // characters and no more than 100 characters.")
  private String description;

  /*
   * Indicates whether the article is published or not.
   * This boolean flag determines if the article is visible to the public or not
   */
  private Boolean isPublished;

  /*
   * List of associated Tags that the Article belongs to.
   * This field stores the tags that categorize or describe the article's content.
   */
  private List<String> tags;

  private Image image;

}
