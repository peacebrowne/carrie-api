package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.example.carrie.entities.Article;

@Mapper
public interface ArticleMapper {

    @Select("SELECT * FROM articles WHERE title =#{title}")
    List<Article> findByTitle(@Param("title") String title);

    @Select("SELECT * FROM articles WHERE id = #{id}::uuid")
    Article findById(@Param("id") String id);

    @Select("SELECT * FROM articles ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Article> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("<script>" +
            "SELECT COUNT(*) AS total FROM (SELECT * FROM articles" +
            "  <where>" +
            "    <if test='authorId != null and authorId != \"\"'>" +
            "      authorId = #{authorId}::uuid" +
            "    </if>" +
            "  </where>" +
            "  ORDER BY #{sort}" +
            ")" +
            "</script>")
    Long totalArticles(@Param("sort") String sort, @Param("authorId") String authorId);

    @Select("SELECT * FROM articles WHERE authorId =#{authorId}::uuid ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Article> findAuthorsArticles(@Param("authorId") String authorId, @Param("sort") String sort,
            @Param("limit") Long limit, @Param("start") Long start);

    @Select("INSERT INTO articles (title, authorId, content) VALUES (#{title}, #{authorId}::uuid, #{content}) RETURNING *")
    Article addArticle(Article article);

    @Select("UPDATE articles SET title = #{title}, content = #{content} WHERE id = #{id}::uuid RETURNING *")
    Article editArticle(Article article);

    @Select("DELETE FROM articles WHERE id = #{id}::uuid RETURNING *")
    Article deleteArticle(@Param("id") String id);

}
