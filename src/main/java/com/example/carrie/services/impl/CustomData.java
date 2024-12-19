package com.example.carrie.services.impl;

import java.util.List;

public class CustomData {
  private Long total;
  private List<?> values;

  public CustomData(Long total, List<?> values) {
    this.total = total;
    this.values = values;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public List<?> getValues() {
    return values;
  }

  public void setValues(List<?> value) {
    this.values = value;
  }

}
