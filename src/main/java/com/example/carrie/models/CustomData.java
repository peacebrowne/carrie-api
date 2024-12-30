package com.example.carrie.models;

import java.util.List;

import lombok.Data;

@Data

public class CustomData {

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

  /*
   * Constructor for CustomData.
   * Initializes the total count and the list of data values.
   *
   * @param total the total count of data items
   * 
   * @param values the list of data items
   */
  public CustomData(Long total, List<?> values) {
    this.total = total;
    this.values = values;
  }
}
