package com.example.carrie.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carrie.success.Success;
import com.example.carrie.dto.ClapDto;
import com.example.carrie.models.Clap;
import com.example.carrie.services.impl.ClapServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/claps")
@CrossOrigin

public class ClapController {

  private final ClapServiceImpl clapServiceImpl;

  public ClapController(ClapServiceImpl clapServiceImpl) {
    this.clapServiceImpl = clapServiceImpl;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findClapById(@PathVariable String id) {
    Clap data = clapServiceImpl.findById(id);
    return Success.OK("Successfully Retrieved Clap", data);
  }

  @GetMapping("/")
  public ResponseEntity<?> getClaps(@RequestParam String targetType, @RequestParam String targetID) {

    ClapDto data = clapServiceImpl.getTotalClaps(targetType, targetID);
    return Success.OK("Successfully Retrieved Total Claps", data);
  }

  @PostMapping
  public ResponseEntity<?> addClap(@RequestBody Clap clap) {
    Clap data = clapServiceImpl.addClap(clap);
    return Success.CREATED("Successfully Created Claps", data);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArticle(@PathVariable String id) {
    Clap data = clapServiceImpl.deleteClap(id);
    return Success.OK("Successfully Deleted Article.", data);
  }

}
