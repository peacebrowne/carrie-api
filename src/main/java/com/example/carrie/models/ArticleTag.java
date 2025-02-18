package com.example.carrie.models;

import lombok.Data;

@Data
public class ArticleTag {
  /*
   * ID of an Article Tag.
   * This field represents a unique identifier for the article tag.
   */
  private String id;

  /*
   * ID of the Article that the Tag is associated with.
   * This field links the tag to a specific article by its ID.
   */
  private String articleID;

  /*
   * ID of the Tag.
   * This field stores the unique identifier of the tag itself.
   */
  private String tagID;

}
