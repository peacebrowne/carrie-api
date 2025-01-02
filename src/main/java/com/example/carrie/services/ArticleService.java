package com.example.carrie.services;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.entities.Article;

public interface ArticleService {
  public Article getArticleById(String id);

  public CustomDto getAllArticles(String sort, Long limit, Long start, Boolean published);

  public Article addArticle(Article article);

  public CustomDto getAuthorsArticles(String authorID, String sort, Long limit, Long start, Boolean published);

  public Article editArticle(Article article, String id);

  public Article deleteArticle(String id);

  public CustomDto searchArticles(String term, String authorID, String sort, Long limit, Long start);

}
