package com.example.carrie.services;

import org.springframework.web.multipart.MultipartFile;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;

public interface ArticleService {
  public Article getArticleById(String id);

  public CustomDto getAllArticles(String sort, Long limit, Long start, String status, String startDate, String endDate);

  public Article addArticle(Article article, MultipartFile image);

  public CustomDto getAuthorsArticles(String authorID, String sort, Long limit, Long start, String status, String startDate, String endDate);

  public Article editArticle(Article article, String id);

  public Article deleteArticle(String id);

  public CustomDto searchArticles(String term, String authorID, String sort, Long limit, Long start, String status, String startDate, String endDate);

}
