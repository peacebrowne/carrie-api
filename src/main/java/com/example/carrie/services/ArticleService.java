package com.example.carrie.services;

import java.util.List;

import com.example.carrie.entities.Article;
import com.example.carrie.entities.CustomData;

public interface ArticleService {
  public Article getArticleById(String id);

  public CustomData getAllArticles(String sort, Long limit, Long start);

  public Article addArticle(Article article);

  public CustomData getAuthorsArticles(String authorID, String sort, Long limit, Long start);

  public Article editArticle(Article article, String id);

  public Article deleteArticle(String id);

  public CustomData searchArticles(String term, String authorID, String sort, Long limit, Long start);

}
