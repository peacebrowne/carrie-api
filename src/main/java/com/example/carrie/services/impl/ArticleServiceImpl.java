package com.example.carrie.services.impl;

import com.example.carrie.dto.ReadingList;
import com.example.carrie.enumerators.ArticleStatus;
import com.example.carrie.exceptions.custom.BadRequest;
import com.example.carrie.exceptions.custom.Conflict;
import com.example.carrie.exceptions.custom.InternalServerError;
import com.example.carrie.exceptions.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.mappers.ImageMapper;
import com.example.carrie.mappers.TagMapper;
import com.example.carrie.models.ReadingHistory;
import com.example.carrie.services.ArticleService;
import com.example.carrie.success.Success;
import com.example.carrie.utils.validations.UUIDValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;
import com.example.carrie.models.Author;
import com.example.carrie.models.Tag;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class ArticleServiceImpl extends ImageServiceImpl implements ArticleService {
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;
  private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
  private final TagServiceImpl tagServiceImpl;
  private final JobServiceImpl jobServiceImpl;

    public ArticleServiceImpl(
            ArticleMapper articleMapper, AuthorMapper authorMapper,
            TagMapper tagMapper, ImageMapper imageMapper, JobServiceImpl jobServiceImpl) {
    super(imageMapper);
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
    this.tagServiceImpl = new TagServiceImpl(tagMapper);
    this.jobServiceImpl = jobServiceImpl;
    }

  public Article getArticleByTitle(String title) {

    try {

      String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);
      String searchTitle = decodedTitle.replace("-", " ");

      Article article = articleMapper.findByTitle(searchTitle);

      if (article == null){
        throw new NotFound("Article with this title does not exist!");
      }

      // Retrieve the list of tags associated with the article
      List<String> articleTags = tagServiceImpl.getArticleTags(article.getId());

      // Set the retrieved tags to the article
      article.setTags(articleTags);

      // Return the article with its associated tags
      return article;

    } catch (NotFound e) {
      log.error("ERROR: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
              "An unexpected error occurred while fetching an Article.");
    }

  }


  @Override
  public Article getArticleById(String id) {

    try {

      // Validate the existence of the article using its ID and return the article if
      // it exists.
      Article article = validateArticle(id);

      // Retrieve the list of tags associated with the article
      List<String> articleTags = tagServiceImpl.getArticleTags(id);

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
  public CustomDto getAllArticles(
      String sort,
      Long limit,
      Long start,
      String status,
      String startDate,
      String endDate) {

    try {

      // Get total count for all articles
      Long total = articleMapper.totalArticles(
          null, null, sort, status,
          formatDateTime(startDate), formatDateTime(endDate));

      // Get all articles
      List<Article> articles = articleMapper.findAll(
          sort, limit, start, status,
          formatDateTime(startDate), formatDateTime(endDate));

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      // Encapsulate the total count and the list of articles
      return new CustomDto(total, articles);

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching the Articles.");
    }
  }

  public Article addArticle(Article article, MultipartFile image) {

    try {

      String title = article.getTitle();
      List<String> tagNames = article.getTags();

      // Create tag if not exists.
      List<Tag> tags = tagServiceImpl.addTags(tagNames);

      // Retrieve all existing articles with the same Title
      Article existingArticle = articleMapper.findByTitle(title);

      // Validate Author
      validateAuthor(article.getAuthorID());
      validateArticleStatus(article.getStatus());

      if (existingArticle != null && Objects.equals(existingArticle.getAuthorID(), article.getAuthorID()) &&
              Objects.equals(existingArticle.getTitle(), article.getTitle()))
        throw new Conflict(
                "An article with the same title already exists for the specified author." +
                        " Please use a unique title or update the existing article.");


      // Checks if an article with given Title and Author already exist.
      /*Optional.ofNullable(existingArticles).ifPresent((articles) -> articles.forEach((a) -> {

        if (Objects.equals(a.getAuthorID(), article.getAuthorID()) &&
            Objects.equals(a.getTitle(), article.getTitle()))
          throw new Conflict(
              "An article with the same title already exists for the specified author. Please use a unique title or update the existing article.");
      }));*/

      // Create new article
      Article createdArticle = articleMapper.addArticle(article);
      createdArticle.setTags(tagNames);

      // Add article's image
      addImage(image, createdArticle.getId(), "article");

      // Creates a connection between articles and tags
      tagServiceImpl.addArticleTags(tags, createdArticle.getId());
      return createdArticle;

    } catch (BadRequest | Conflict | NotFound e) {
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
  public CustomDto getAuthorsArticles(
      String authorID,
      String sort,
      Long limit,
      Long start,
      String status,
      String startDate,
      String endDate) {
    try {

      // Validate Author
      validateAuthor(authorID);

      // Get total articles for a particular author by the ID
      Long total = articleMapper.totalAuthorArticles(
          authorID, sort, status,
          formatDateTime(startDate), formatDateTime(endDate));

      // Get all articles associated with an author by ID
      List<Article> articles = articleMapper.findAuthorsArticles(
          authorID, sort, limit, start, status,
          formatDateTime(startDate), formatDateTime(endDate));

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      // Encapsulate the total count and the list of an Author's article
      return new CustomDto(total, articles);

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
  public CustomDto getAuthorsInterestedArticles(
      String authorID,
      Long limit,
      Long start) {
    try {

      // Validate Author
      validateAuthor(authorID);

      // Get total articles for a particular author by the ID
      Long total = articleMapper.totalAuthorInterestArticles(authorID);

      // Get all articles associated with an author by ID
      List<Article> articles = articleMapper.findAuthorInterestedArticles(authorID, limit, start);

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      // Encapsulate the total count and the list of an Author's article
      return new CustomDto(total, articles);

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
  public Article editArticle(Article article, MultipartFile image, String id) {
    try {
      Article existingArticle = getArticleById(id);

      // 1. ONLY check the DB if the title in the request is actually different
      // from what is already saved in 'existingArticle'
      if (article.getTitle() != null && !article.getTitle().equalsIgnoreCase(existingArticle.getTitle())) {

        boolean isDuplicate = articleMapper.existsByTitleIgnoreCase(article.getTitle(), id);

        if (isDuplicate) {
          // If it's an auto-save, you might want to return a specific error
          // so the frontend can show a "Title taken" warning without crashing
          throw new BadRequest("This title is already in use.");
        }
        existingArticle.setTitle(article.getTitle());
      }

      // 2. Update other fields (Content, Description, etc.)
      // These don't need uniqueness checks, so they are safe to auto-save
      Optional.ofNullable(article.getContent()).ifPresent(existingArticle::setContent);
      Optional.ofNullable(article.getStatus()).ifPresent(existingArticle::setStatus);
      Optional.ofNullable(article.getDescription()).ifPresent(existingArticle::setDescription);

      existingArticle.setUpdatedAt(LocalDateTime.now());

      // 3. Save to DB
      articleMapper.editArticle(existingArticle);

      return existingArticle;

    } catch (BadRequest e) {
      throw e; // Pass through the "Title Taken" error
    } catch (Exception e) {
      log.error("Auto-save failed: {}", e.getMessage());
      throw new InternalServerError("Update failed.");
    }
  }

  /*public Article editArticle(Article article, MultipartFile image, String id) {

    try {
      // Retrieve the existing article by its ID
      Article existingArticle = getArticleById(id);

      Article articleWithTitle = articleMapper.findByTitle(article.getTitle());
      if (
              articleWithTitle != null &&
              Objects.equals(articleWithTitle.getTitle(), article.getTitle())
              && !Objects.equals(articleWithTitle.getAuthorID(), article.getAuthorID()))
      {
        throw new BadRequest("This title is already in use.");
      }

      // Update the article's data if a new value is provided
      Optional.ofNullable(article.getContent()).ifPresent(existingArticle::setContent);
      Optional.ofNullable(article.getTitle()).ifPresent(existingArticle::setTitle);
      Optional.ofNullable(article.getStatus()).ifPresent(existingArticle::setStatus);
      Optional.ofNullable(article.getPublishedAt()).ifPresent(existingArticle::setPublishedAt);
      Optional.ofNullable(article.getDescription())
          .ifPresent(existingArticle::setDescription);

      // Set the current timestamp as the updated date for the article
      LocalDateTime currentDate = LocalDateTime.now();
      existingArticle.setUpdatedAt(currentDate);

      // Update articles
      articleMapper.editArticle(existingArticle);

      // Add article's image
      addImage(image, existingArticle.getId(), "article");

      // Update the article's tags with the new list and return the updated tag names
      List<String> tagNames = tagServiceImpl.editArticleTags(id, article.getTags());

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
*/
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
  public CustomDto searchArticles(
      String term,
      String authorID,
      String sort,
      Long limit,
      Long start,
      String status,
      String startDate,
      String endDate) {
    try {
      validateUUID(authorID);

      Long total = articleMapper.totalSearchArticles(
          term, authorID, sort, status,
          formatDateTime(startDate), formatDateTime(endDate));

      List<Article> articles = articleMapper.search(
          term, authorID, sort, limit, start, status,
          formatDateTime(startDate), formatDateTime(endDate));

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      return new CustomDto(total, articles);

    } catch (BadRequest e) {
      log.error("Bad Request: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while searching for an Article.");
    }

  }

  @Override
  public List<Article> getArticlesByAuthorInterest(String authorID, Long limit, Long start) {
    try {
      validateAuthor(authorID);

      List<Article> articles = articleMapper.findArticlesByAuthorInterest(authorID, limit, start);

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      return articles;
    } catch (BadRequest e) {
      log.error("Bad Request: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching Articles based on Author's Interest.");
    }

  }

  private List<String> getArticleTags(String id) {
    return tagServiceImpl.getArticleTags(id);
  }

  private void validateUUID(String id) {
    if (!UUIDValidator.isValidUUID(id)) {
      throw new BadRequest("Invalid ID");
    }
  }

  private void validateAuthor(String authorID) {

    // Validate Author ID
    validateUUID(authorID);

    Author author = authorMapper.findById(authorID);
    if (author == null) {
      throw new NotFound("Author does not exist!");
    }
  }

  private Article validateArticle(String articleID) {
    // Validate Article ID
    validateUUID(articleID);

    Optional<Article> article = articleMapper.findById(articleID);

    if (article.isEmpty()) {
      throw new NotFound("Article does not exist!");
    }

    return article.get();
  }

  private void validateArticleStatus(String status) {
    ArticleStatus articleStatus = ArticleStatus.valueOf(status.toUpperCase());

    if (!Arrays.asList(ArticleStatus.DRAFT, ArticleStatus.PENDING, ArticleStatus.PUBLISHED).contains(articleStatus)) {
      String validStatuses = Arrays.asList(ArticleStatus.DRAFT, ArticleStatus.PENDING, ArticleStatus.PUBLISHED)
          .toString();
      throw new BadRequest("Invalid Article status. It should be one of: " + validStatuses);
    }
  }

  private LocalDateTime formatDateTime(String datetime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return datetime != null ? LocalDateTime.parse(datetime, formatter) : null;
  }

  @Override
  public CustomDto getArticleByTag(String tagId, String authorId , Long limit, Long start) {
    try {
      Long total = articleMapper.totalTagArticles(tagId, authorId);

      List<Article> articles = articleMapper.findByTag(tagId, authorId, limit, start);
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      return new CustomDto(total, articles);

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching an Article by Tag.");
    }
  }

   @Override
   public Map<String, Object> getArticleAnalytics(String id) {
    try {
      Map<String, Object> analyticsDto = articleMapper.getTotalArticleAnalytics(id);
      System.out.println(analyticsDto);

      return analyticsDto;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching Article Analytics.");
    }
  }

    @Override
    public ReadingList getReadingListEntry(String authorId, String articleId) {
        try {
            validateArticle(articleId);
            validateAuthor(authorId);
            return articleMapper.getList(authorId, articleId);
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching Article Analytics.");
        }
    }

    @Override
    public ReadingList addToReadingList(String authorId, String articleId) {
        try {

            Optional<ReadingList> existingList = Optional.ofNullable(getReadingListEntry(authorId, articleId));
            if (existingList.isPresent()){
                throw new BadRequest("This article has already been added to User reading list");
            }

            return articleMapper.addToReadingList(articleId, authorId);
        } catch (BadRequest | NotFound e) {
            log.error("Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while adding Article to reading list.");
        }
    }

    @Override
    public CustomDto getUserReadingList(String authorId) {
        try {
            validateAuthor(authorId);
            Long total = articleMapper.totalUserReadingList(authorId);
            List<ReadingList> readingList = articleMapper.findUserReadingList(authorId);

            return new CustomDto(total, readingList);

        } catch (BadRequest | NotFound e) {
            log.error("Bad Request: {}", e.getMessage(), e);
            throw e;
        }  catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching Saved Articles.");
        }
    }

    @Override
    public ReadingList removeFromReadingList(String authorId, String articleId) {
        try {

            Optional<ReadingList> existingList = Optional.ofNullable(getReadingListEntry(authorId, articleId));
            if (existingList.isEmpty()){
                throw new NotFound("This article has not been added to User reading list");
            }

            return articleMapper.removeFromReadingList(authorId, articleId);
        } catch (NotFound | BadRequest e) {
            log.error("Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while Removing Article from saved list.");
        }
    }

    public void publishArticle(String articleId) {
        articleMapper.publishArticle(articleId);
    }

    public Date publishArticleLater(String articleId, String scheduledTime) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(scheduledTime, formatter);
            articleMapper.pendingArticle(articleId, dateTime);
            return jobServiceImpl.scheduleArticlePublish(articleId, dateTime);

        } catch (NotFound e) {
            log.error("Not Found Request: {}", e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while scheduling date-time to publish article.");
        }

    }

    public Map<String, Object> shareArticle(String articleId, String sharedBy) {
    try {

      validateArticle(articleId);
      validateAuthor(sharedBy);
      return articleMapper.shareArticle(articleId, sharedBy);

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while sharing an Article.");
    }
  }

  public List<Map<String, Object>> getSharesByArticle(String articleId) {
    try {

      validateArticle(articleId);
      return articleMapper.getSharesByArticle(articleId);

    } catch (BadRequest | NotFound e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
          "An unexpected error occurred while fetching shared Articles.");
    }
  }

  @Async
  public ReadingHistory addUserReadingHistory(String userId, String articleId) {
    try {



      List.of(userId, articleId).forEach(this::validateUUID);

      // Debug logging to show this is running on a different thread
      log.info("Async Logged history for User: {} Article: {}, on thread: {}",
              userId, articleId, Thread.currentThread().getName());

      // This database call happens on an Async worker thread
      return articleMapper.addReadHistory(userId, articleId);

    } catch (BadRequest e) {
      log.error("Error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
              "An unexpected error occurred while adding Articles reading list.");
    }

  }

  // @Override
  public CustomDto getAuthorPersonalizedFeeds(
          String authorID,
          Long limit,
          Long start) {
    try {

      // Validate Author
      validateAuthor(authorID);

      // Get total articles for a particular author by the ID
      Long total = articleMapper.totalFindUserPersonalizedFeeds(authorID);

      // Get all articles associated with an author by ID
      List<Article> articles = articleMapper.findUserPersonalizedFeeds(authorID, limit, start);

      // Add related tags to articles
      articles.forEach(article -> article.setTags(getArticleTags(article.getId())));

      // Encapsulate the total count and the list of an Author's article
      return new CustomDto(total, articles);

    } catch (NotFound e) {
      log.error("Not Found: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage(), e);
      throw new InternalServerError(
              "An unexpected error occurred while fetching an Author's Article.");
    }

  }


}