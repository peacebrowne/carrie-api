package com.example.carrie.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.carrie.exceptions.custom.BadRequest;
import com.example.carrie.exceptions.custom.Conflict;
import com.example.carrie.exceptions.custom.NotFound;
import com.example.carrie.services.TagService;
import com.example.carrie.utils.validations.UUIDValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.models.Tag;
import com.example.carrie.exceptions.custom.InternalServerError;
import com.example.carrie.mappers.TagMapper;

@Transactional
@Service
public class TagServiceImpl implements TagService {
  private final TagMapper tagMapper;
  private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

  public TagServiceImpl(
      TagMapper tagMapper) {
    this.tagMapper = tagMapper;
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

        tags.add(existingTag.orElseGet(() -> tagMapper.addTags(newTag)));

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

        /*
         * Insert the ArticleTag mapping into the database to associate the tag with the article
         */
        tagMapper.addArticleTag(articleID, tag.getId());
        tag.setStories(tag.getStories() + 1);
        tagMapper.updateTag(tag);

      });
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while editing an Article.");

    }
  }

  protected List<String> getArticleTags(String articleID) {
    try {

      /*
       * Iterate through the list of article objects and return a list of names for
       * each article
       * Return the tag name
       */
      return tagMapper.getArticleTags(articleID).stream().map(Tag::getName).collect(Collectors.toList());

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

      // Add the tags if id does not exist
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
    return null;

  }

  protected void deleteArticleTag(String articleID) {
    try {
      /*
       * Delete the ArticleTag mapping into the database to disassociate the tag with
       * the article
       */
      tagMapper.deleteArticleTag(articleID);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while deleting an Article Tag.");

    }
  }

  protected void deleteAuthorInterest(String authorID) {
    try {
      /*
       * Delete the ArticleTag mapping into the database to disassociate the tag with
       * the article
       */
      tagMapper.deleteAuthorInterests(authorID);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while deleting an Author Interest.");

    }
  }

  protected Tag addAuthorInterest(String authorID, List<Tag> interests) {
      Tag updatedTag = null;
      try {
          for (Tag interest : interests) {

              Optional<Tag> existingTag = tagMapper.getSingleAuthorInterest(authorID, interest.getId());
              if (existingTag.isPresent()) {

                  // Return the Conflict with the specific, helpful message
                  throw new Conflict(String.format(
                          "You are already following the tag '%s'. Please choose a different interest to follow.",
                          interest.getName()
                  ));
              }

              // Insert the author-tag relationship
              tagMapper.addAuthorInterest(authorID, interest.getId());
              updatedTag = tagMapper.updateTag(interest);
          }

          // Return the last updated tag (assuming one tag in the list)
          return updatedTag;
     } catch ( Conflict e) {
          log.error("Conflict: {}", e.getMessage(), e);
          throw e;
      } catch (Exception e) {
          log.error("Internal Server Error: {}", e.getMessage(), e);
          throw new InternalServerError(
             "An unexpected error occurred while creating an account.");
     }
  }

  protected List<String> editAuthorInterest(String authorID, List<String> interests) {

    // Retrieve all the tags associated with the article by ID
    Optional<List<String>> interestToUpdate = Optional.ofNullable(interests);

    // Check if the tags to be updated are provided.
    if (interestToUpdate.isPresent()) {

      // Add the tags if id does not exist
      List<Tag> updatedInterest = addTags(interestToUpdate.get());

      /*
       * Delete the Author Interest mapping into the database to disassociate the tag
       * with the article
       */
      deleteAuthorInterest(authorID);

      /*
       * Insert the new Author Interest mapping into the database to associate the tag
       * with the article
       */
      addAuthorInterest(authorID, updatedInterest);

      return interestToUpdate.get();

    }
    return null;

  }

  protected List<String> getAuthorInterest(String authorID) {
    try {

      /*
       * Iterate through the list of author objects and return a list of names for
       * each author
       * Return the tag name
       */
      return tagMapper.getAuthorTags(authorID).stream().map(Tag::getName).collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching the Author Tags.");

    }
  }

  @Override
  public List<Tag> recommendedAuthorInterests(String authorID, Long limit) {
      try {

          validateUUID(authorID);

          return tagMapper.getAuthorRecommendedInterest(authorID, limit);

      } catch (Exception e) {
          log.error("Internal Server Error: {}", e.getMessage(), e);
          throw new InternalServerError(
                  "An unexpected error occurred while fetching the Recommended Author Interests.");

      }
  }

    @Override
    public List<Tag> randomRecommendedTags(String parentTagId, String tagId, Long limit) {
        try {

            List.of(tagId, parentTagId).forEach(this::validateUUID);

            return tagMapper.getRandomTags(parentTagId, tagId, limit);

        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the Random Recommended Tags.");

        }
    }

  @Override
  public List<Tag> getAllTags() {
    try {
      return tagMapper.getAll();
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while getting all tags");
    }
  }

  @Override
  public Tag getTagById(String id) {
      try {
          return tagMapper.getById(id);
      } catch (Exception e) {
          log.error("Internal Server Error: {}", e.getMessage(), e);
          throw new InternalServerError(
                  "An unexpected error occurred while getting all tags");
      }
  }

  @Override
  public List<Tag> searchTags(String term) {
        try {
            return tagMapper.searchTags(term);
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while searching tags");
        }
    }

  @Override
  public Tag followTag(String tagId, String authorId) {
        try {
            List.of(tagId, authorId).forEach(this::validateUUID);
            Tag tag = getTagById(tagId);
            tag.setPopularity(tag.getPopularity() + 1);
            return addAuthorInterest(authorId, List.of(tag));
        }
        catch ( Conflict e) {
            log.error("Conflict: {}", e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while updating tag followers");
        }
  }

  @Override
  public Tag unfollowTag(String tagId, String authorId) {
        try {
            List.of(tagId, authorId).forEach(this::validateUUID);
            Tag tag = getTagById(tagId);

            if (tag == null) throw new NotFound("The Tag you're trying to unfollow does not exits");

            Optional<Tag> existingTag = tagMapper.getSingleAuthorInterest(authorId, tagId);
            if (existingTag.isEmpty()) {

                // Return the Conflict with the specific, helpful message
                throw new NotFound("You are currently not following this tag");
            }
            tagMapper.deleteAuthorInterest(authorId, tagId);

            tag.setPopularity(tag.getPopularity() - 1);
            return tagMapper.updateTag(tag);
        }
        catch ( NotFound e) {
            log.error("Not found: {}", e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while updating tag followers");
        }
  }

    private void validateUUID(String id) {
        if (!UUIDValidator.isValidUUID(id)) {
            throw new BadRequest("Invalid Author ID");
        }
    }



}
