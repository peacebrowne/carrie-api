package com.example.carrie.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Tag {

  /*
   * ID of the tag.
   * This field represents a unique identifier for the tag.
   */
  private String id;

  /*
   * Name of the tag.
   * This field stores the name associated with the tag.
   */
  private String name;

  /*
  * Tag total popularity.
  * This field stores the total number authors associated with the tag.
  */
  private Long popularity = 0L;

  /*
  * Tag total stories.
  * This field stores the total number articles associated with the tag.
  */
  private Long stories = 0L;

    /*
     * ID of the comment that is replied to.
     * This field links the replies to a specific comment by its ID.
     */
    private String parentTagId;

}
