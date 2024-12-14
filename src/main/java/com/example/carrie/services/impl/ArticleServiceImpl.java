package com.example.carrie.services.impl;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.ArticleMapper;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.services.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carrie.entities.Article;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
  private final ArticleMapper articleMapper;
  private final AuthorMapper authorMapper;

  @Autowired
  public ArticleServiceImpl(ArticleMapper articleMapper, AuthorMapper authorMapper) {
    this.articleMapper = articleMapper;
    this.authorMapper = authorMapper;
  }

  @Override
  public Article getArticleById(String id) {
    return articleMapper.findById(id);
  }

  @Override
  public List<Article> getAllArticles(String sort, String order, Long limit, Long start) {
    return articleMapper.findAll(sort, order, limit, start);
  }

  @Override
  public Article addArticle(Article article) {

    String title = article.getTitle();

    Article articleExist = articleMapper.findByTitle(title);

    if (articleExist != null) {
      throw new BadRequest("An article with this " + title + "already exist!");
    }

    return articleMapper.addArticle(article);
  }

  @Override
  public List<Article> getAuthorsArticles(String authorId) {

    boolean authorNotExist = authorMapper.findById(authorId).getEmail().isEmpty();

    if (authorNotExist) {
      throw new NotFound("Author does not exist!");
    }
    return articleMapper.findAuthorsArticles(authorId);

  }

  @Override
  public Article editArticle(Article article, String id) {
    articleMapper.editArticle(article.getTitle(), article.getContent(), id);
    return this.getArticleById(id);
  }

  @Override
  public Article deleteArticle(String id) {
    return articleMapper.deleteArticle(id);
  }
}