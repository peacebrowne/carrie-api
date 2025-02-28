package com.example.carrie.services;

import com.example.carrie.dto.ClapDto;
import com.example.carrie.models.Clap;

public interface ClapService {

  public Clap findById(String id);

  public Clap addClap(Clap clap, String action);

  public Clap deleteClap(String id);

  public ClapDto getTotalClaps(String targetType, String targetID);

}
