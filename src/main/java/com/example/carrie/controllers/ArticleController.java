package com.example.carrie.controllers;

import java.util.List;

import com.example.carrie.success.Success;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.carrie.entities.Article;
import com.example.carrie.services.impl.ArticleServiceImpl;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
  private final ArticleServiceImpl articleServiceImpl;

  public ArticleController(ArticleServiceImpl articleServiceImpl) {
    this.articleServiceImpl = articleServiceImpl;
  }

  @GetMapping
  public ResponseEntity<?> getAllArticles(
      @RequestParam(required = false, defaultValue = "created_at") String sort,
      @RequestParam(required = false, defaultValue = "ASC") String order,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    List<Article> articles = articleServiceImpl.getAllArticles(sort, order, limit, start);
    return Success.OK("Successfully Retrieved all Articles.", articles);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findArticleById(@PathVariable String id) {

    Article article = articleServiceImpl.getArticleById(id);
    List<Article> data = List.of(article);
    return Success.OK("Successfully Retrieved single Article.", data);
  }

  @GetMapping("/{id}/authors")
  public ResponseEntity<?> authorArticles(@PathVariable String id) {
    List<Article> authorArticles = articleServiceImpl.getAuthorsArticles(id);
    return Success.OK("Successfully Retrieved Author's Articles.", authorArticles);
  };

  @PostMapping
  public ResponseEntity<?> addArticle(@RequestBody Article article) {
    Article createdArticle = articleServiceImpl.addArticle(article);
    List<Article> data = List.of(createdArticle);
    return Success.CREATED("Successfully Created Article.", data);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editArticle(@RequestBody Article article, @PathVariable String id) {
    Article editedArticle = articleServiceImpl.editArticle(article, id);
    List<Article> data = List.of(editedArticle);

    return Success.OK("Successfully Updated Article.", data);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArticle(@PathVariable String id) {

    Article deletedArticle = articleServiceImpl.deleteArticle(id);
    List<Article> data = List.of(deletedArticle);

    return Success.OK("Successfully Deleted Article.", data);
  }

}
