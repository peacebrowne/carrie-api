package com.example.carrie.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.carrie.models.Image;
import com.example.carrie.services.impl.ImageServiceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

  @PostMapping
  public ResponseEntity<byte[]> addImage(
          @RequestParam String id,
          MultipartFile img,
          @RequestParam String type
  ) throws IOException {

    System.out.println(id);

      Image image = imageServiceImpl.addImage(img, id,  type);

    return image.getData() != null
            ? ResponseEntity.ok().contentType(MediaType.valueOf(image.getType())).body(image.getData())
            : ResponseEntity.ok().body(
            image.getData());
  }

}
