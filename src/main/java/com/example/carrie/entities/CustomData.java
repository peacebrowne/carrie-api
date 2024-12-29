package com.example.carrie.entities;

import java.util.List;

import lombok.Data;

@Data
public class CustomData {
  private Long total;
  private List<?> values;

  public CustomData(Long total, List<?> values) {
    this.total = total;
    this.values = values;
  }

}
