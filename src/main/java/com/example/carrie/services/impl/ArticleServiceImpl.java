package com.example.carrie.services.impl;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.services.ArticleService;
import com.example.carrie.utils.UUIDValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.entities.Article;
import com.example.carrie.entities.Author;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;
  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

  public ArticleServiceImpl(ArticleMapper articleMapper, AuthorMapper authorMapper) {
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
  }

  @Override
  public Article getArticleById(String id) {

    try {
      if (!UUIDValidator.isValidUUID(id)) {
        throw new BadRequest("Invalid article ID");
      }

      Optional<Article> article = Optional.ofNullable(articleMapper.findById(id));

      if (article.isEmpty()) {
        throw new NotFound("Article with this id '" + id + "' does not exist!");
      }

      return article.get();

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occured while fetching an Article.");

    }

  }

  @Override
  public List<?> getAllArticles(String sort, Long limit, Long start) {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    try {

      Long total = articleMapper.totalArticles(sort, null);
      List<Article> articles = articleMapper.findAll(sort, limit, start);

      CustomData customData = new CustomData(total, articles);

      return List.of(customData);

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occured while fetching the Articles.");
    }
  }

  @Override
  public Article addArticle(Article article) {

    try {
      String title = article.getTitle();

      List<Article> existingArticles = articleMapper.findByTitle(title);

      if (!UUIDValidator.isValidUUID(article.getAuthorId())) {
        throw new BadRequest("Invalid author ID");
      }

      Optional.ofNullable(existingArticles).ifPresent((articles) -> articles.forEach((a) -> {

        if (Objects.equals(a.getAuthorId(), article.getAuthorId()) &&
            Objects.equals(a.getTitle(), article.getTitle())) {
          throw new BadRequest("An Article with this title already exist for this Author!");
        }
      }));

      return articleMapper.addArticle(article);

    } catch (BadRequest e) {
      log.error("Bad Request: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(
          "Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occured while adding an Article");
    }
  }

  @Override
  public List<?> getAuthorsArticles(String authorId, String sort, Long limit, Long start) {

    try {

      Optional<Author> author = Optional.ofNullable(authorMapper.findById(authorId));

      if (author.isEmpty()) {
        throw new NotFound("Author does not exist!");
      }

      Long total = articleMapper.totalArticles(sort, authorId);
      List<Article> articles = articleMapper.findAuthorsArticles(authorId, sort, limit, start);

      CustomData customData = new CustomData(total, articles);

      return List.of(customData);

    } catch (NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occured while fetching an Author's Article.");

    }

  }

  @Override
  public Article editArticle(Article article, String id) {

    try {
      Article existingArticle = getArticleById(id);

      Optional.ofNullable(article.getContent()).ifPresent(content -> existingArticle.setContent(content));
      Optional.ofNullable(article.getTitle()).ifPresent(title -> existingArticle.setTitle(title));

      return articleMapper.editArticle(existingArticle);

    } catch (Exception e) {

      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occured while editing an Article.");

    }
  }

  @Override
  public Article deleteArticle(String id) {
    try {

      getArticleById(id);
      return articleMapper.deleteArticle(id);

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the author.");
    }
  }
}