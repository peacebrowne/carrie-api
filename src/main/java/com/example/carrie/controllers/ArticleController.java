package com.example.carrie.controllers;

import java.util.List;

import com.example.carrie.success.Success;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.carrie.entities.Article;
import com.example.carrie.entities.CustomData;
import com.example.carrie.errors.custom.BadRequest;
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
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Boolean published,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomData data = articleServiceImpl.getAllArticles(sort, limit, start, published);
    return Success.OK("Successfully Retrieved all Articles.", data);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findArticleById(@PathVariable String id) {

    List<Article> data = List.of(articleServiceImpl.getArticleById(id));
    return Success.OK("Successfully Retrieved single Article.", data);
  }

  @GetMapping("/authors/{id}")
  public ResponseEntity<?> authorArticles(@PathVariable String id,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Boolean published,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomData data = articleServiceImpl.getAuthorsArticles(id, sort, limit, start, published);

    return Success.OK("Successfully Retrieved Author's Articles.", data);
  };

  @GetMapping("/authors/{id}/search")
  public ResponseEntity<?> searchAuthorArticles(@PathVariable String id,
      @RequestParam String term,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomData data = articleServiceImpl.searchArticles(term, id, sort, limit, start);

    return Success.OK("Successfully Retrieved Author's Articles.", data);
  };

  @GetMapping("/search")
  public ResponseEntity<?> searchArticles(
      @RequestParam String term,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {

    CustomData data = articleServiceImpl.searchArticles(term, null, sort, limit, start);

    return Success.OK("Successfully Retrieved Author's Articles.", data);
  };

  @PostMapping
  public ResponseEntity<?> addArticle(@Valid @RequestBody Article article, BindingResult result) {

    if (result.hasErrors()) {
      throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());
    }

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
