package com.example.carrie.services;

import com.example.carrie.entities.Image;

public interface ImageService {

  public Image getImageById(String id);

  public Image getImageByTarget(String targetID);

  public Image addImage(String url, String targetType);

}
