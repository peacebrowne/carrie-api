package com.example.carrie.services;

import com.example.carrie.dto.AnalyticsDto;
import com.example.carrie.dto.ReadingList;
import org.springframework.web.multipart.MultipartFile;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    Article getArticleById(String id);

     CustomDto getAllArticles(String sort, Long limit, Long start, String status, String startDate,
            String endDate);

     Article addArticle(Article article, MultipartFile image);

     CustomDto getAuthorsArticles(String authorID, String sort, Long limit, Long start, String status,
            String startDate, String endDate);

     CustomDto getAuthorsInterestedArticles(String authorID, Long limit, Long start);

     Article editArticle(Article article, MultipartFile image, String id);

     Article deleteArticle(String id);

     CustomDto searchArticles(String term, String authorID, String sort, Long limit, Long start, String status,
            String startDate, String endDate);

    List<Article> getArticlesByAuthorInterest(String authorID, Long limit, Long start);

    ReadingList getReadingListEntry(String authorId, String articleId);

    CustomDto getArticleByTag(String tagId, String authorId, Long limit, Long start);

    AnalyticsDto getArticleAnalytics(String id);

    ReadingList addToReadingList(String authorId, String articleId);

    CustomDto getUserReadingList(String authorId);

    ReadingList removeFromReadingList(String authorId, String articleId);

}
