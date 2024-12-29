package com.example.carrie.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.carrie.entities.ArticleTag;
import com.example.carrie.entities.Tag;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.mappers.ArticleTagMapper;
import com.example.carrie.mappers.TagMapper;

@Service
public class TagServiceImpl {
  private final TagMapper tagMapper;
  private final ArticleTagMapper articleTagMapper;
  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

  public TagServiceImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
    this.tagMapper = tagMapper;
    this.articleTagMapper = articleTagMapper;
  }

  protected List<Tag> addTags(List<String> tagNames) {

    try {
      ArrayList<Tag> tags = new ArrayList<>();

      for (String name : tagNames) {

        Optional<Tag> existingTag = Optional.ofNullable(tagMapper.getByName(name));
        Tag newTag = new Tag();
        newTag.setName(name);

        tags.add(existingTag.isEmpty() ? tagMapper.addTags(newTag) : existingTag.get());

      }

      return tags;

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while adding a Tag.");

    }

  }

  protected void addArticleTags(List<Tag> tags, String articleID) {
    try {
      tags.forEach(tag -> {
        ArticleTag articleTag = new ArticleTag();
        articleTag.setArticleID(articleID);
        articleTag.setTagID(tag.getId());
        articleTagMapper.addArticleTag(articleTag);
      });
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while editing an Article.");

    }
  }

  protected List<String> getArticleTags(String articleID) {
    try {

      return articleTagMapper.getArticleTags(articleID).stream().map(tag -> {
        return tag.getName();
      }).collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching the Articles Tags.");

    }
  }

  protected List<String> editArticleTags(String articleID, List<String> tags) {

    Optional<List<String>> tagsToUpdate = Optional.ofNullable(tags);

    if (tagsToUpdate.isPresent()) {
      List<Tag> updatedTags = this.addTags(tagsToUpdate.get());

      deleteArticleTag(articleID);
      this.addArticleTags(updatedTags, articleID);

      return tagsToUpdate.get();

    }
    return tags;

  }

  protected void deleteArticleTag(String articleID) {
    try {
      articleTagMapper.deleteArticleTag(articleID);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while deleting an Article Tag.");

    }
  }

}
