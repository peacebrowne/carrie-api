package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.example.carrie.entities.Article;

@Mapper
public interface ArticleMapper {

    @Select("SELECT title FROM articles WHERE title =#{title}")
    List<Article> findByTitle(@Param("title") String title);

    @Select("SELECT * FROM articles WHERE id = #{id}::uuid")
    Article findById(@Param("id") String id);

    @Select("SELECT * FROM articles ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Article> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT * FROM articles WHERE authorId =#{authorId}::uuid")
    List<Article> findAuthorsArticles(@Param("authorId") String authorId);

    @Select("INSERT INTO articles (title, authorId, content) VALUES (#{title}, #{authorId}::uuid, #{content}) RETURNING *")
    Article addArticle(Article article);

    @Select("UPDATE articles SET title = #{title}, content = #{content} WHERE id = #{id}::uuid RETURNING *")
    Article editArticle(Article article);

    @Select("DELETE FROM articles WHERE id = #{id}::uuid")
    Article deleteArticle(@Param("id") String id);

}
