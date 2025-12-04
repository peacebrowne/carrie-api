package com.example.carrie.services;

import com.example.carrie.models.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(String id);

    List<Tag> searchTags(String term);
    List<Tag> recommendedInterests(String authorID, Long limit);

    Tag followTag(String tagId, String authorId);

    Tag unfollowTag(String tagId, String authorId);
}
