package com.example.carrie.entities;

import lombok.Data;

@Data
public class Clap {

  /*
   * ID of the Clap
   */
  private String id;

  /*
   * ID of the Author that clapped for the .
   * This field links the comment to a specific author by its ID.
   */
  private String authorID;

  private String articleID;

  private Long count;

}
