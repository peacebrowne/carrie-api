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
                        "    <if test='authorID != null and authorID != \"\"'>" +
                        "      authorID = #{authorID}::uuid" +
                        "    </if>" +
                        "  </where>" +
                        "  ORDER BY #{sort}" +
                        ")" +
                        "</script>")
        Long totalArticles(@Param("sort") String sort, @Param("authorID") String authorID);

        @Select("SELECT * FROM articles WHERE authorID =#{authorID}::uuid ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
        List<Article> findAuthorsArticles(@Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("limit") Long limit, @Param("start") Long start);

        @Select("INSERT INTO articles (title, authorID, content, is_published, description) VALUES (#{title}, #{authorID}::uuid, #{content}, #{is_published}, #{description}) RETURNING *")
        Article addArticle(Article article);

        @Insert("UPDATE articles SET title = #{title}, content = #{content}, is_published = #{is_published}, description = #{description}, updated_at = #{updated_at} WHERE id = #{id}::uuid")
        void editArticle(Article article);

        @Delete("DELETE FROM articles WHERE id = #{id}::uuid")
        Article deleteArticle(@Param("id") String id);

}
