package com.example.carrie.entities;

import java.time.LocalDateTime;

import lombok.Data;

@Data
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
   * Local date and time the tag was created.
   * This field records the timestamp when the tag was created.
   */
  private LocalDateTime created_at;

}
