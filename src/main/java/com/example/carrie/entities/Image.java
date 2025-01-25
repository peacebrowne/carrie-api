package com.example.carrie.entities;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Image {
  /*
   * ID of the Image
   */
  private String id;

  /*
   * Name of the image
   * This field stores the image name of an Author or an Article.
   */
  private String name;

  /*
   * Type of the image
   * This field stores the image type of Author or an Article.
   * Example: (jpg, jpeg or png).
   */
  private String type;

  /*
   * Data of the image
   * This field stores the image data of an Author or an Article.
   */
  private byte[] data;

  /*
   * ID of the Target that the Image belongs to.
   * This field links the image to a specific Target by its ID.
   * Target could be an Author or an Article.
   */
  private String targetID;

  /*
   * Local date and time the Image was created.
   * This field records the timestamp when the image was first created.
   */
  private LocalDateTime createdAt;

  /*
   * Local date and time the Image was updated.
   * This field stores the timestamp when the image was last updated.
   */
  private LocalDateTime updatedAt;


}
