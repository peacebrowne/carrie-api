package com.example.carrie.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.carrie.mappers.AuthorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.CommentMapper;
import com.example.carrie.dto.CustomDto;
import com.example.carrie.entities.Article;
import com.example.carrie.entities.Author;
import com.example.carrie.entities.Comment;
import com.example.carrie.services.CommentService;
import com.example.carrie.utils.validations.UUIDValidator;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
  private final CommentMapper commentMapper;
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;

  public CommentServiceImpl(CommentMapper commentMapper, ArticleMapper articleMapper, AuthorMapper authorMapper) {
    this.articleMapper = articleMapper;
    this.commentMapper = commentMapper;
    this.authorMapper = authorMapper;
  }

  @Override
  public Comment getCommentById(String id) {
    try {

      // Retrieve the comment from the database
      return validateComment(id, "Comment does not exist!");

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching a Comment.");

    }
  }

  @Override
  public CustomDto getArticleComments(String articleID, Long limit, Long start) {
    try {

      // Validate essential fields
      validateArticle(articleID);

      // Get total comments for a particular article by the ID
      Long total = commentMapper.totalComments(articleID);

      // Get all comments associated with an article by ID
      List<Comment> comments = commentMapper.findArticleComments(articleID, limit, start);

      // Encapsulate the total count and the list of comments
      return new CustomDto(total, comments);

    } catch (NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Article's Comment.");

    }
  }

  @Override
  public Comment addComment(Comment comment) {
    try {

      // Validate parent comment ID if present
      Optional.ofNullable(comment.getParentCommentID()).ifPresent(parentCommentID -> {
        validateComment(comment.getParentCommentID(), "Invalid Parent Comment ID");
      });

      // Ensure the author and article exist
      validateAuthor(comment.getAuthorID());
      validateArticle(comment.getArticleID());

      // Save the comment
      Comment createdComment = commentMapper.addComment(comment);
      return createdComment;

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error while adding comment: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while adding a Comment.");
    }
  }

  @Override
  public Comment editComment(Comment comment, String id) {
    try {
      // Retrieve the existing comment
      Comment existingComment = getCommentById(id);

      // Update Author ID if provided
      Optional.ofNullable(comment.getAuthorID()).ifPresent(authorID -> {
        validateAuthor(comment.getAuthorID());
        existingComment.setAuthorID(comment.getAuthorID());
      });

      // Update Article ID if provided
      Optional.ofNullable(comment.getArticleID()).ifPresent(articleID -> {
        validateArticle(comment.getArticleID());
        existingComment.setArticleID(comment.getArticleID());
      });

      // Update content if provided
      Optional.ofNullable(comment.getContent()).ifPresent(content -> existingComment.setContent(content));

      // Update the timestamp
      existingComment.setUpdatedAt(LocalDateTime.now());

      // Update the comment
      commentMapper.editComment(existingComment);

      return existingComment;

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error while editing comment: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while editing the Comment.");
    }
  }

  @Override
  public Comment deleteComment(String id) {
    try {

      // Check if the comment exists by ID
      Comment comment = getCommentById(id);

      // Delete the comment by ID
      commentMapper.deleteComment(id);
      return comment;

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the Comment.");
    }
  }

  @Override
  public CustomDto getCommentReplies(String parentCommentID, Long limit, Long start) {

    try {

      // Check if comment exists
      getCommentById(parentCommentID);

      // Get total replies for a particular comment by the ID
      Long total = commentMapper.totalComments(parentCommentID);

      // Get all replies associated with a comment by ID
      List<Comment> comments = commentMapper.findCommentReplies(parentCommentID, limit, start);

      // Encapsulate the total count and the list of comments
      CustomDto data = new CustomDto(total, comments);
      return data;

    } catch (NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Comment's replies.");

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

  private Comment validateComment(String commentID, String errorMessage) {
    // Validate Comment ID
    validateUUID(commentID, "Invalid Comment ID");

    Comment comment = commentMapper.findById(commentID);
    if (comment == null) {
      throw new NotFound(errorMessage);
    }

    return comment;
  }

}
