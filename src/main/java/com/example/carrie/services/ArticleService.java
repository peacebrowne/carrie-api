package com.example.carrie.services;

import org.springframework.web.multipart.MultipartFile;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService {
  public Article getArticleById(String id);

  public CustomDto getAllArticles(String sort, Long limit, Long start, String status, String startDate, String endDate);

  public Article addArticle(Article article, MultipartFile image);

  public CustomDto getAuthorsArticles(String authorID, String sort, Long limit, Long start, String status, String startDate, String endDate);

  public Article editArticle(Article article, MultipartFile image, String id);

  public Article deleteArticle(String id);

  public CustomDto searchArticles(String term, String authorID, String sort, Long limit, Long start, String status, String startDate, String endDate);

    List<Article> getArticlesByAuthorInterest(String authorID,
                                              Long limit,
                                              Long start
                                         );

    public CustomDto getArticleByTag(String tag, Long limit, Long start);

  Map<String, Object> getArticleAnalytics(String id);
}
