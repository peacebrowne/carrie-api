package com.example.carrie.dto;

import lombok.Data;

@Data
public class ClapValueDto {
  private String authorID;
  private Long claps;

  public ClapValueDto(String authorID, Long claps) {
    this.authorID = authorID;
    this.claps = claps;
  }

}
