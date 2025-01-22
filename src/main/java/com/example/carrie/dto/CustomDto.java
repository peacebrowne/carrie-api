package com.example.carrie.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomDto {

  /*
   * Total count of the data items.
   * This field represents the total number of items in the dataset.
   */
  private Long total;

  /*
   * List of data values.
   * This is a generic list that holds the actual data items.
   */
  private List<?> values;

  public CustomDto(Long total, List<?> values) {
    this.total = total;
    this.values = values;
  }

}
