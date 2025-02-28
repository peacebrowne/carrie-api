package com.example.carrie.services.impl;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.carrie.models.Image;
import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.mappers.ImageMapper;

@Service
@Transactional
public class ImageServiceImpl {

  private final ImageMapper imageMapper;
  private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

  public ImageServiceImpl(ImageMapper imageMapper) {
    this.imageMapper = imageMapper;
  }

  public Image getImageById(String id) {

    try {
      return imageMapper.findById(id);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Image.");

    }

  }

  public Image getImageByTarget(String targetID) {

    try {
      Optional<Image> image = imageMapper.findImageByTarget(targetID);

      return image.orElseGet(Image::new);

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching a target Image.");

    }

  }

  protected void addImage(MultipartFile image, String targetID, String targetType) throws IOException {
    try {

      validateImage(image.getContentType(), targetType);

      Image img = new Image();
      img.setTargetID(targetID);
      img.setName(image.getOriginalFilename());
      img.setData(image.getBytes());
      img.setType(image.getContentType());
      imageMapper.addImage(img);

    } catch (BadRequest | IOException e) {
      log.error("Bad image format: {}", e.getMessage(), e);
      throw e;
    }

    catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while adding an Image.");

    }
  }

  private void validateImage(String imageType, String targetType) {
    if (!Arrays.asList("article", "author").contains(targetType.toLowerCase())) {
      throw new BadRequest("Invalid target type. Must be 'article' or 'author'.");
    }

    String type = imageType.split("/")[1].toLowerCase();

    if (!Arrays.asList("jpg", "jpeg", "png", "webp").contains(type)) {
      throw new BadRequest("Invalid image type. Must be 'jpg','jpeg','png' or 'webp'");
    }

  }

}
