package com.example.carrie.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.carrie.models.Image;
import com.example.carrie.services.impl.ImageServiceImpl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/images")
@RestController
@CrossOrigin
public class ImageController {

  private final ImageServiceImpl imageServiceImpl;

  public ImageController(ImageServiceImpl imageServiceImpl) {
    this.imageServiceImpl = imageServiceImpl;
  }

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> getImageByTarget(@PathVariable String id) {
    Image image = imageServiceImpl.getImageByTarget(id);

    return image.getData() != null
        ? ResponseEntity.ok().contentType(MediaType.valueOf(image.getType())).body(image.getData())
        : ResponseEntity.ok().body(
            image.getData());

  }

}
