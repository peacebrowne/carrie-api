package com.example.carrie.services.impl;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.ArticleTagMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.mappers.TagMapper;
import com.example.carrie.services.ArticleService;
import com.example.carrie.utils.UUIDValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carrie.entities.Article;
import com.example.carrie.entities.ArticleTag;
import com.example.carrie.entities.Author;
import com.example.carrie.entities.Tag;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;
  private final TagMapper tagMapper;
  private final ArticleTagMapper articleTagMapper;
  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

  public ArticleServiceImpl(
      ArticleMapper articleMapper,
      AuthorMapper authorMapper,
      TagMapper tagMapper,
      ArticleTagMapper articleTagMapper) {
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
    this.tagMapper = tagMapper;
    this.articleTagMapper = articleTagMapper;
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

      List<String> articleTags = getArticleTags(id);
      article.get().setTags(articleTags);

      return article.get();

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
  public CustomData getAllArticles(String sort, Long limit, Long start) {

    try {

      Long total = articleMapper.totalArticles(sort, null);
      List<Article> articles = articleMapper.findAll(sort, limit, start);

      // Add related tags to articles
      articles.forEach(article -> {
        List<String> tags = getArticleTags(article.getId());
        article.setTags(tags);
      });

      CustomData data = new CustomData(total, articles);

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
      List<Tag> tags = this.addTags(tagNames);

      List<Article> existingArticles = articleMapper.findByTitle(title);

      if (!UUIDValidator.isValidUUID(article.getAuthorID())) {
        throw new BadRequest("Invalid author ID");
      }

      Optional<Author> author = Optional.ofNullable(authorMapper.findById(article.getAuthorID()));

      if (author.isEmpty()) {
        throw new NotFound("Author does not exist!");
      }

      // Checks if an article with given Title and Author already exist.
      Optional.ofNullable(existingArticles).ifPresent((articles) -> articles.forEach((a) -> {

        if (Objects.equals(a.getAuthorID(), article.getAuthorID()) &&
            Objects.equals(a.getTitle(), article.getTitle())) {
          throw new BadRequest("An Article with this title already exist for this Author!");
        }
      }));

      Article createdArticle = articleMapper.addArticle(article);
      createdArticle.setTags(tagNames);

      // Creates a connection between articles and tags
      this.addArticleTags(tags, createdArticle.getId());

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
  public CustomData getAuthorsArticles(String authorID, String sort, Long limit, Long start) {

    try {

      Optional<Author> author = Optional.ofNullable(authorMapper.findById(authorID));

      if (author.isEmpty()) {
        throw new NotFound("Author does not exist!");
      }

      Long total = articleMapper.totalArticles(sort, authorID);
      List<Article> articles = articleMapper.findAuthorsArticles(authorID, sort, limit, start);

      // Add related tags to articles
      articles.forEach(article -> {
        List<String> tags = getArticleTags(article.getId());
        article.setTags(tags);
      });

      CustomData data = new CustomData(total, articles);

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
      Article existingArticle = getArticleById(id);

      Optional.ofNullable(article.getContent()).ifPresent(content -> existingArticle.setContent(content));
      Optional.ofNullable(article.getTitle()).ifPresent(title -> existingArticle.setTitle(title));
      Optional.ofNullable(article.getIs_published()).ifPresent(published -> existingArticle.setIs_published(published));
      Optional.ofNullable(article.getDescription())
          .ifPresent(description -> existingArticle.setDescription(description));

      LocalDateTime currentDate = LocalDateTime.now();
      existingArticle.setUpdated_at(currentDate);

      articleMapper.editArticle(existingArticle);

      List<String> tagNames = editArticleTags(id, article.getTags());

      existingArticle.setTags(tagNames);

      return existingArticle;

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
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

      Article article = getArticleById(id);
      articleMapper.deleteArticle(id);
      return article;

    } catch (BadRequest | NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError("An unexpected error occurred while deleting the author.");
    }
  }

  private List<Tag> addTags(List<String> tagNames) {

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

  private void addArticleTags(List<Tag> tags, String articleID) {
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

  private List<String> getArticleTags(String articleID) {
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

  private List<String> editArticleTags(String articleID, List<String> tags) {

    Optional<List<String>> tagsToUpdate = Optional.ofNullable(tags);

    if (tagsToUpdate.isPresent()) {
      List<Tag> updatedTags = this.addTags(tagsToUpdate.get());

      deleteArticleTag(articleID);
      this.addArticleTags(updatedTags, articleID);

      return tagsToUpdate.get();

    }
    return tags;

  }

  // TODO Delete article tags
  private void deleteArticleTag(String articleID) {
    try {
      articleTagMapper.deleteArticleTag(articleID);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while deleting an Article Tag.");

    }
  }

  // TODO Search article by tags

}