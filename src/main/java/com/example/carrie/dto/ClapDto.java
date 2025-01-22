package com.example.carrie.dto;

import java.util.List;

import lombok.Data;

@Data
public class ClapDto {

  /*
   * Represents the type of target for the data, e.g., "article" or "comment".
   * Helps differentiate whether the data is associated with an article or a
   * comment.
   */
  private String targetType;

  /*
   * Represents the unique identifier of the target (e.g., article ID or comment
   * ID). This links the data to a specific article or comment in the system.
   */
  private String targetID;

  /*
   * Holds the total count or value for the target.
   */
  private Long total;

  /*
   * A list of individual authors who clapped, including their IDs and how many
   * claps they contributed
   */
  private List<ClapValueDto> values;

  public ClapDto(String targetType, String targetID, Long total, List<ClapValueDto> values) {
    this.targetType = targetType;
    this.targetID = targetID;
    this.total = total;
    this.values = values;
  }

}
