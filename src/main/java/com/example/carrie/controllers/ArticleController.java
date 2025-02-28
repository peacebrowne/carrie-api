package com.example.carrie.controllers;

import java.util.Collections;
import java.util.List;

import com.example.carrie.success.Success;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;
import com.example.carrie.errors.custom.BadRequest;
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
            sort, limit, start, status, startDate, endDate
    );
    return Success.OK("Successfully Retrieved all Articles.", data);
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
            id, sort, limit, start, status, startDate, endDate
    );
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
            term, id, sort, limit, start, status, startDate, endDate
    );
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
            term, null, sort, limit, start, status, startDate, endDate
            );
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

    List<Article> data = Collections.singletonList(articleServiceImpl.addArticle(article, image));
    return Success.CREATED("Successfully Created Article.", data);

  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editArticle(@RequestBody Article article, @PathVariable String id) {
    List<Article> data = Collections.singletonList(articleServiceImpl.editArticle(article, id));
    return Success.OK("Successfully Updated Article.", data);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArticle(@PathVariable String id) {
    List<Article> data = Collections.singletonList(articleServiceImpl.deleteArticle(id));
    return Success.OK("Successfully Deleted Article.", data);
  }

}
