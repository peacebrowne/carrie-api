package com.example.carrie.services;

import java.util.List;

import com.example.carrie.entities.Article;

public interface ArticleService {
  public Article getArticleById(String id);

  public List<?> getAllArticles(String sort, Long limit, Long start);

  public Article addArticle(Article article);

  public List<?> getAuthorsArticles(String authorId, String sort, Long limit, Long start);

  public Article editArticle(Article article, String id);

  public Article deleteArticle(String id);

}
