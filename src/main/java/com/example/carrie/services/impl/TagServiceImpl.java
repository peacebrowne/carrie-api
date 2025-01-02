package com.example.carrie.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.entities.ArticleTag;
import com.example.carrie.entities.Tag;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.mappers.ArticleTagMapper;
import com.example.carrie.mappers.TagMapper;

@Transactional
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
      // Create an ArrayList to hold the final list of tags
      ArrayList<Tag> tags = new ArrayList<>();

      // Iterate over the provided tag names
      for (String name : tagNames) {

        // Check if a tag with the given name already exists.
        Optional<Tag> existingTag = Optional.ofNullable(tagMapper.getByName(name));

        // Create a new Tag if the tag does not already exist
        Tag newTag = new Tag();
        newTag.setName(name);

        /**
         * If the tag does not exist, add it to the database and then add it to the
         * list. Otherwise, add the existing tag to the list.
         */
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
      // Iterate over the provided tag names
      tags.forEach(tag -> {
        /**
         * Create a new instance of ArticleTag to establish the relationship between
         * an article and a tag
         */
        ArticleTag articleTag = new ArticleTag();

        // Set the article ID for the relationship
        articleTag.setArticleID(articleID);

        // Set the tag ID for the relationship
        articleTag.setTagID(tag.getId());

        /*
         * Insert the ArticleTag mapping into the database to associate the tag with the
         * article
         */
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

      /**
       * Iterate through the list of article objects and return a list of names for
       * each article
       */
      return articleTagMapper.getArticleTags(articleID).stream().map(tag -> {
        // Return the tag name
        return tag.getName();
      }).collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching the Articles Tags.");

    }
  }

  protected List<String> editArticleTags(String articleID, List<String> tags) {

    // Retrieve all the tags associated with the article by ID
    Optional<List<String>> tagsToUpdate = Optional.ofNullable(tags);

    // Check if the tags to be updated are provided.
    if (tagsToUpdate.isPresent()) {

      // Add the tags if do not exist
      List<Tag> updatedTags = addTags(tagsToUpdate.get());

      /*
       * Delete the ArticleTag mapping into the database to disassociate the tag with
       * the article
       */
      deleteArticleTag(articleID);

      /*
       * Insert the new ArticleTag mapping into the database to associate the tag with
       * the article
       */
      addArticleTags(updatedTags, articleID);

      return tagsToUpdate.get();

    }
    return tags;

  }

  protected void deleteArticleTag(String articleID) {
    try {
      /*
       * Delete the ArticleTag mapping into the database to disassociate the tag with
       * the article
       */
      articleTagMapper.deleteArticleTag(articleID);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while deleting an Article Tag.");

    }
  }

}
