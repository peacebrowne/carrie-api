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
import com.example.carrie.entities.Article;
import com.example.carrie.entities.Author;
import com.example.carrie.entities.Comment;
import com.example.carrie.models.CustomData;
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
      if (!UUIDValidator.isValidUUID(id))
        throw new BadRequest("Invalid comment ID");

      Optional<Comment> comment = Optional.ofNullable(commentMapper.findById(id));

      if (comment.isEmpty())
        throw new NotFound("Comment with this id '" + id + "' does not exist!");

      return comment.get();

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching a Comment.");

    }
  }

  @Override
  public CustomData getArticleComments(String articleID, Long limit, Long start) {
    try {
      Optional<Article> article = Optional.ofNullable(articleMapper.findById(articleID));

      if (article.isEmpty())
        throw new NotFound("Article does not exist!");

      Long total = commentMapper.totalComments(articleID);
      List<Comment> comments = commentMapper.findArticleComments(articleID, limit, start);

      CustomData data = new CustomData(total, comments);

      return data;

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

      if (!UUIDValidator.isValidUUID(comment.getArticleID()))
        throw new BadRequest("Invalid Article ID");

      if (!UUIDValidator.isValidUUID(comment.getAuthorID()))
        throw new BadRequest("Invalid Author ID");

      Optional<Author> author = Optional.ofNullable(authorMapper.findById(comment.getAuthorID()));

      if (author.isEmpty())
        throw new NotFound("Author of this comment does not exist!");

      Optional<Article> article = Optional.ofNullable(articleMapper.findById(comment.getArticleID()));

      if (article.isEmpty())
        throw new NotFound("Article does not exist!");

      Comment createdComment = commentMapper.addComment(comment);

      return createdComment;
    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(
          "Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while adding a Comment");
    }
  }

  @Override
  public Comment editComment(Comment comment, String id) {
    try {
      Comment existingComment = getCommentById(id);

      Optional.ofNullable(comment.getAuthorID()).ifPresent(authorID -> {
        if (!UUIDValidator.isValidUUID(authorID))
          throw new BadRequest("Invalid Author ID");

        Optional<Author> author = Optional.ofNullable(authorMapper.findById(comment.getAuthorID()));

        if (author.isEmpty())
          throw new NotFound("Author of this comment does not exist!");

        existingComment.setAuthorID(authorID);
      });

      Optional.ofNullable(comment.getArticleID()).ifPresent(articleID -> {
        if (!UUIDValidator.isValidUUID(articleID))
          throw new BadRequest("Invalid Article ID");

        Optional<Article> article = Optional.ofNullable(articleMapper.findById(comment.getArticleID()));

        if (article.isEmpty())
          throw new NotFound("Article with this" + comment.getArticleID() + " does not exist!");

        existingComment.setArticleID(articleID);
      });

      Optional.ofNullable(comment.getContent()).ifPresent(content -> existingComment.setContent(content));

      LocalDateTime currentDate = LocalDateTime.now();
      existingComment.setUpdated_at(currentDate);

      commentMapper.editComment(existingComment);

      return existingComment;

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {

      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while editing a Comment.");

    }
  }

  @Override
  public Comment deleteComment(String id) {
    try {

      Comment comment = getCommentById(id);
      commentMapper.deleteComment(id);
      return comment;

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the Comment.");
    }
  }

}
