package com.example.carrie.services.impl;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.Conflict;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.ArticleTagMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.mappers.TagMapper;
import com.example.carrie.services.ArticleService;
import com.example.carrie.utils.validations.UUIDValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.entities.Article;
import com.example.carrie.entities.Author;
import com.example.carrie.entities.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ArticleServiceImpl extends TagServiceImpl implements ArticleService {
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;
  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

  public ArticleServiceImpl(
      ArticleMapper articleMapper,
      AuthorMapper authorMapper,
      TagMapper tagMapper,
      ArticleTagMapper articleTagMapper) {
    super(tagMapper, articleTagMapper);
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
  }

  @Override
  public Article getArticleById(String id) {

    try {

      /*
       * Validate the existence of the article using its ID and return the article if
       * it exist.
       */
      Article article = validateArticle(id);

      // Retrieve the list of tags associated with the article
      List<String> articleTags = getArticleTags(id);

      // Set the retrieved tags to the article
      article.setTags(articleTags);

      // Return the article with its associated tags
      return article;

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Article.");

    }

  }

  @Override
  public CustomDto getAllArticles(String sort, Long limit, Long start, Boolean published) {

    try {

      // Get total count for all articles
      Long total = articleMapper.totalArticles(null, null, sort, published);

      // Get all articles
      List<Article> articles = articleMapper.findAll(sort, limit, start, published);

      // Add related tags to articles
      articles.forEach(article -> {
        List<String> tags = getArticleTags(article.getId());
        article.setTags(tags);
      });

      // Encapsulate the total count and the list of articles
      CustomDto data = new CustomDto(total, articles);

      return data;

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching the Articles.");
    }
  }

  @Override
  public Article addArticle(Article article) {

    try {
      String title = article.getTitle();
      List<String> tagNames = article.getTags();

      // Create tag if not exists.
      List<Tag> tags = addTags(tagNames);

      // Retrieve all existing articles with the same Title
      List<Article> existingArticles = articleMapper.findByTitle(title);

      // Validate Author
      validateAuthor(article.getAuthorID());

      // Checks if an article with given Title and Author already exist.
      Optional.ofNullable(existingArticles).ifPresent((articles) -> articles.forEach((a) -> {

        if (Objects.equals(a.getAuthorID(), article.getAuthorID()) &&
            Objects.equals(a.getTitle(), article.getTitle()))
          throw new Conflict(
              "An article with the same title already exists for the specified author. Please use a unique title or update the existing article.");

      }));

      // Create new article
      Article createdArticle = articleMapper.addArticle(article);
      createdArticle.setTags(tagNames);

      // Creates a connection between articles and tags
      addArticleTags(tags, createdArticle.getId());
      return createdArticle;

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(
          "Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while adding an Article");
    }
  }

  @Override
  public CustomDto getAuthorsArticles(String authorID, String sort, Long limit, Long start, Boolean published) {

    try {

      // Validate Author
      validateAuthor(authorID);

      // Get total articles for a particular author by the ID
      Long total = articleMapper.totalArticles(null, authorID, sort, published);

      // Get all articles associated with an author by ID
      List<Article> articles = articleMapper.findAuthorsArticles(authorID, sort, limit, start, published);

      // Add related tags to articles
      articles.forEach(article -> {
        List<String> tags = getArticleTags(article.getId());
        article.setTags(tags);
      });

      // Encapsulate the total count and the list of an Author's article
      CustomDto data = new CustomDto(total, articles);
      return data;

    } catch (NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Author's Article.");

    }

  }

  @Override
  public Article editArticle(Article article, String id) {

    try {
      // Retrieve the existing article by its ID
      Article existingArticle = getArticleById(id);

      // Update the article's data if a new value is provided
      Optional.ofNullable(article.getContent()).ifPresent(content -> existingArticle.setContent(content));
      Optional.ofNullable(article.getTitle()).ifPresent(title -> existingArticle.setTitle(title));
      Optional.ofNullable(article.getIsPublished()).ifPresent(published -> existingArticle.setIsPublished(published));
      Optional.ofNullable(article.getDescription())
          .ifPresent(description -> existingArticle.setDescription(description));

      // Set the current timestamp as the updated date for the article
      LocalDateTime currentDate = LocalDateTime.now();
      existingArticle.setUpdatedAt(currentDate);

      // Update articles
      articleMapper.editArticle(existingArticle);

      // Update the article's tags with the new list and return the updated tag names
      List<String> tagNames = editArticleTags(id, article.getTags());

      // Set the updated tags to the existing article
      existingArticle.setTags(tagNames);

      return existingArticle;

    } catch (BadRequest | NotFound e) {
      log.error("Validation Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {

      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while editing an Article.");

    }
  }

  @Override
  public Article deleteArticle(String id) {
    try {

      // Check if the article exists by ID
      Article article = getArticleById(id);

      // Delete the article by id
      articleMapper.deleteArticle(id);
      return article;

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the Article.");
    }
  }

  @Override
  public CustomDto searchArticles(String term, String authorID, String sort, Long limit, Long start) {
    try {
      if (authorID != null && !UUIDValidator.isValidUUID(authorID))
        throw new BadRequest("Invalid Author ID");

      Long total = articleMapper.totalArticles(term, authorID, sort, null);

      List<Article> articles = articleMapper.search(term, authorID, sort, limit, start);

      // Add related tags to articles
      articles.forEach(article -> {
        List<String> tags = getArticleTags(article.getId());
        article.setTags(tags);
      });

      CustomDto data = new CustomDto(total, articles);

      return data;

    } catch (BadRequest e) {
      log.error("Bad Request: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while searching for an Article.");

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

  private Article validateArticle(String articleID) {
    // Validate Article ID
    validateUUID(articleID, "Invalid Article ID");

    Article article = articleMapper.findById(articleID);
    if (article == null) {
      throw new NotFound("Article does not exist!");
    }

    return article;
  }
}