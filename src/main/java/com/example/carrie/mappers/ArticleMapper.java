package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.example.carrie.entities.Article;

@Mapper
public interface ArticleMapper {

    @Select("SELECT title FROM articles WHERE title =#{title}")
    Article findByTitle(@Param("title") String title);

    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article findById(@Param("id") String id);

    @Select("SELECT * FROM articles ORDER BY #{sort} #{order} LIMIT #{limit} OFFSET #{start}")
    List<Article> findAll(@Param("sort") String sort, @Param("order") String order, @Param("limit") Long limit,
            @Param("start") Long start);

    @Select("SELECT * FROM articles WHERE authorId =#{authorId}")
    List<Article> findAuthorsArticles(@Param("authorId") String authorId);

    @Insert("INSERT INTO articles (id, title, authorId, content) VALUES (#{id}, #{title}, #{authorId}, #{content}) RETURNING *")
    Article addArticle(Article article);

    @Update("UPDATE articles SET title = #{title}, content = #{content} WHERE id = #{id} RETURNING *")
    Article editArticle(@Param("title") String title, @Param("content") String content, @Param("id") String id);

    @Delete("DELETE FROM articles WHERE id = #{id}")
    Article deleteArticle(@Param("id") String id);

}
