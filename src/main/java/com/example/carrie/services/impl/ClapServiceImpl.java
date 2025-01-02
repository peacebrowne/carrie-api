package com.example.carrie.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.dto.ClapDto;
import com.example.carrie.dto.ClapValueDto;
import com.example.carrie.entities.Article;
import com.example.carrie.entities.Author;
import com.example.carrie.entities.Clap;
import com.example.carrie.entities.Comment;
import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.Conflict;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.mappers.ClapMapper;
import com.example.carrie.mappers.CommentMapper;
import com.example.carrie.services.ClapService;
import com.example.carrie.utils.validations.UUIDValidator;

@Service
@Transactional
public class ClapServiceImpl implements ClapService {

  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
  private final CommentMapper commentMapper;
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;
  private final ClapMapper clapMapper;

  public ClapServiceImpl(CommentMapper commentMapper, ArticleMapper articleMapper, AuthorMapper authorMapper,
      ClapMapper clapMapper) {
    this.commentMapper = commentMapper;
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
    this.clapMapper = clapMapper;
  }

  @Override
  public Clap findById(String id) {

    try {

      validateUUID(id, "Invalid Clap ID");

      Clap clap = clapMapper.findById(id);

      if (clap == null) {
        throw new NotFound("Clap does not exist!");
      }

      return clap;

    } catch (BadRequest | NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while adding a Clap.");
    }

  }

  @Override
  public Clap addClap(Clap clap) {
    try {

      // Validate Clap Data
      validateClap(clap);

      Clap existingClaps = null;

      if (clap.getArticleID() != null) {
        existingClaps = clapMapper.findClapByAuthorAndTarget(clap.getAuthorID(), clap.getArticleID());
      } else if (clap.getCommentID() != null) {
        existingClaps = clapMapper.findClapByAuthorAndTarget(clap.getAuthorID(), clap.getCommentID());
      }

      if (existingClaps != null) {
        updateClapCount(existingClaps);
        return existingClaps;
      }

      // Save the clap
      return clapMapper.addClap(clap);

    } catch (BadRequest | Conflict | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while adding a Clap.");
    }
  }

  @Override
  public Clap deleteClap(String id) {
    try {

      Clap existingClap = findById(id);

      // Delete the Clap
      clapMapper.deleteClap(id);
      return existingClap;

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error while deleting Clap: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the Clap.");
    }
  }

  @Override
  public ClapDto getTotalClaps(String targetType, String targetID) {
    try {
      validateTarget(targetType, targetID);

      List<Clap> claps = clapMapper.findClapByTarget(targetID);

      Long total = calculateTotalClaps(claps);

      List<ClapValueDto> clapsByAuthors = claps.stream().map(clap -> {
        return new ClapValueDto(clap.getAuthorID(), clap.getCount());
      }).collect(Collectors.toList());

      return new ClapDto(targetType, targetID, total, clapsByAuthors);

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error while deleting Clap: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while getting total Clap counts.");

    }
  }

  private void validateUUID(String id, String errorMessage) {
    if (!UUIDValidator.isValidUUID(id)) {
      throw new BadRequest(errorMessage);
    }
  }

  private void validateAuthor(String authorID) {
    // Validate Author ID
    validateUUID(authorID, "Invalid Author ID");

    Author author = authorMapper.findById(authorID);
    if (author == null) {
      throw new NotFound("Author does not exist!");
    }
  }

  private void validateArticle(String articleID) {
    // Validate Article ID
    validateUUID(articleID, "Invalid Article ID");

    Article article = articleMapper.findById(articleID);
    if (article == null) {
      throw new NotFound("Article does not exist!");
    }
  }

  private void validateComment(String commentID) {
    // Validate Comment ID
    validateUUID(commentID, "Invalid Comment ID");

    Comment comment = commentMapper.findById(commentID);
    if (comment == null) {
      throw new NotFound("Comment does not exist!");
    }
  }

  private void validateClap(Clap clap) {
    // Check if the author exists
    validateAuthor(clap.getAuthorID());

    // Validate and check if the clap is for an article or a comment
    if (clap.getArticleID() == null && clap.getCommentID() == null
        || clap.getArticleID() != null && clap.getCommentID() != null) {
      throw new BadRequest("You must provide an ID for either an Article or a Comment, but not both.");
    }

    if (clap.getArticleID() != null) {
      validateArticle(clap.getArticleID());
    }

    if (clap.getCommentID() != null) {
      validateComment(clap.getCommentID());
    }

  }

  private void validateTarget(String targetType, String targetID) {
    // Validate target ID.
    validateUUID(targetID, "Invalid Target ID");

    if (!List.of("article", "comment").contains(targetType.toLowerCase())) {
      throw new BadRequest("Invalid target type. Must be 'article' or 'comment'.");
    }

    // Checks if the request is for an article or a comment.
    switch (targetType.toLowerCase()) {
      case "article":
        validateArticle(targetID);
        break;
      case "comment":
        validateComment(targetID);
        break;
    }
  }

  private Long calculateTotalClaps(List<Clap> claps) {

    // Iterate through the article claps
    return claps.stream().map(clap -> {

      // Returns the total clap count of each clap
      return clap.getCount();
    }).reduce((long) 0, (result, element) -> {

      return result + element;
    });
  }

  private void updateClapCount(Clap existingClap) {

    try {

      // Update the Clap count based on the input``
      Long currentCount = existingClap.getCount() != null ? existingClap.getCount() : 0;
      existingClap.setCount(++currentCount);

      // Set the current timestamp as the updated date for the article
      LocalDateTime currentDate = LocalDateTime.now();
      existingClap.setUpdatedAt(currentDate);

      // Update the Clap count
      clapMapper.updateClapCount(existingClap.getId(), currentCount);

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error while updating Clap count: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while updating Clap count.");
    }
  }

}
