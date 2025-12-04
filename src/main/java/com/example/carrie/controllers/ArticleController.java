package com.example.carrie.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.carrie.success.Success;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;
import com.example.carrie.exceptions.custom.BadRequest;
import com.example.carrie.services.impl.ArticleServiceImpl;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api/articles")
public class ArticleController {

  private final ArticleServiceImpl articleServiceImpl;

  public ArticleController(ArticleServiceImpl articleServiceImpl) {
    this.articleServiceImpl = articleServiceImpl;
  }

  @GetMapping
  public ResponseEntity<?> getAllArticles(
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomDto data = articleServiceImpl.getAllArticles(
        sort, limit, start, status, startDate, endDate);
    return Success.OK("Successfully Retrieved all Articles.", data);
  }

  @GetMapping("/{id}/article-analytics")
  public ResponseEntity<?> getArticleAnalytics(@PathVariable String id) {
    Map<String, Object> data = articleServiceImpl.getArticleAnalytics(id);
    return Success.OK("Successfully Retrieved Article Analytics", data);
  }

  @GetMapping("/tag/{tagId}/author/{authorId}")
  public ResponseEntity<?> getArticleByTags(
          @PathVariable String tagId,
          @PathVariable String authorId,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {
    CustomDto data = articleServiceImpl.getArticleByTag(tagId, authorId, limit, start);
    return Success.OK("Successfully Retrieved Article with tags", data);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getArticleById(@PathVariable String id) {

    Article data = articleServiceImpl.getArticleById(id);
    return Success.OK("Successfully Retrieved single Article.", data);
  }

  @GetMapping("/authors/{id}")
  public ResponseEntity<?> getAuthorArticles(@PathVariable String id,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomDto data = articleServiceImpl.getAuthorsArticles(
        id, sort, limit, start, status, startDate, endDate);
    return Success.OK("Successfully Retrieved Author's Articles.", data);
  }

  @GetMapping("/authors/{id}/search")
  public ResponseEntity<?> searchAuthorArticles(@PathVariable String id,
      @RequestParam String term,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomDto data = articleServiceImpl.searchArticles(
        term, id, sort, limit, start, status, startDate, endDate);
    return Success.OK("Successfully Retrieved Author's Articles.", data);
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchArticles(
      @RequestParam String term,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomDto data = articleServiceImpl.searchArticles(
        term, null, sort, limit, start, status, startDate, endDate);
    return Success.OK("Successfully Retrieved Author's Articles.", data);
  }

  @PostMapping
  public ResponseEntity<?> addArticle(
      @Valid @RequestPart Article article,
      @RequestPart(required = false) MultipartFile image,
      BindingResult result) {

    if (result.hasErrors()) {
      throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());
    }

    Article data = articleServiceImpl.addArticle(article, image);
    return Success.CREATED("Successfully Created Article.", data);

  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editArticle(
      @RequestPart Article article,
      @RequestPart(required = false) MultipartFile image,
      @PathVariable String id
  ) {

    Article data = articleServiceImpl.editArticle(article, image, id);
    return Success.OK("Successfully Updated Article.", data);
  }

  @PutMapping("/{id}/publish-now")
  public ResponseEntity<?> publishArticleNow(@PathVariable String id){
       articleServiceImpl.publishArticle(id);
      return Success.OK("Article Successfully Published", true);
  }

  @PutMapping("/{id}/publish-later")
    public ResponseEntity<?> publishArticleLater(@PathVariable String id, @RequestParam String date){
        Date scheduledDate = articleServiceImpl.publishArticleLater(id, date);
        return Success.OK("Article Successfully Published", date);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArticle(@PathVariable String id) {
    List<Article> data = Collections.singletonList(articleServiceImpl.deleteArticle(id));
    return Success.OK("Successfully Deleted Article.", data);
  }

  @PostMapping("/share")
  public ResponseEntity<?> shareArticle(@RequestBody Map<String, Object> payload) {
    String articleId = payload.get("articleId").toString();
    String sharedBy = payload.get("sharedBy").toString();

    Map<String, Object> sharedArticle = articleServiceImpl.shareArticle(articleId, sharedBy);
    return Success.CREATED("Successfully shared the article", sharedArticle);
  }

  @GetMapping("/shares/{articleId}")
  public ResponseEntity<?> getShares(@PathVariable String articleId) {
    return Success.OK("Successfully retrieved shared article",
        articleServiceImpl.getSharesByArticle(articleId));
  }

  @GetMapping("/author/{id}/interests")
  public ResponseEntity<?> getArticlesByAuthorInterest(@PathVariable String id,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {
    return Success.OK("Successfully Retrieved Author Interested Articles",
        articleServiceImpl.getAuthorsInterestedArticles(id, limit, start));
  }

    // CREATE - Add to reading list
    @PostMapping("/save")
    public ResponseEntity<?> addToReadingList(
            @RequestParam String authorId, @RequestParam String articleId) {
      return Success.OK("Successfully Added To Reading List",
         articleServiceImpl.addToReadingList(authorId, articleId));
    }

    // READ - Get all saved articles for a user
    @GetMapping("/saved/{authorId}")
    public ResponseEntity<?> getUserReadingList(@PathVariable String authorId) {
      return Success.OK("Successfully Retrieved To Reading List",
            articleServiceImpl.getUserReadingList(authorId));
    }

    // DELETE - Remove by readingListId
    @DeleteMapping("/unsave")
    public ResponseEntity<?>  removeFromReadingList(
            @RequestParam String authorId, @RequestParam String articleId) {

        return Success.OK("Successfully removed from reading list",
                articleServiceImpl.removeFromReadingList(authorId, articleId));
    }

}
