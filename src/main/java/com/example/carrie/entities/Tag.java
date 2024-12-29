package com.example.carrie.entities;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Tag {

  private String id;
  private String name;
  private LocalDateTime created_at;
}
