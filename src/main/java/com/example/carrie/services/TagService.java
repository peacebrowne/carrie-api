package com.example.carrie.services;

import com.example.carrie.models.Tag;

import java.util.List;

public interface TagService {
    public List<Tag> getAllTags();
    public Tag getTagById(String id);

    List<Tag> searchTags(String term);
}
