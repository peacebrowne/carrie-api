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

        @Select("<script> " +
                        "SELECT * FROM articles a " +
                        "<if test='published != null and published != \"\"'>" +
                        "  WHERE a.is_published = #{published}" +
                        "</if>" +
                        "<choose> " +
                        "    <when test='sort == \"title\"'> " +
                        "      ORDER BY a.title " +
                        "    </when> " +
                        "    <when test='sort == \"updated_at\"'> " +
                        "      ORDER BY a.updated_at DESC " +
                        "    </when> " +
                        "    <otherwise> " +
                        "      ORDER BY a.created_at DESC " +
                        "    </otherwise> " +
                        "</choose>  " +
                        "LIMIT #{limit} OFFSET #{start}" +
                        "</script>"

        )
        List<Article> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start,
                        @Param("published") Boolean published);

        @Select("<script> " +
                        "SELECT COUNT(*) AS total FROM ( " +
                        "  SELECT DISTINCT a.* " +
                        "  FROM articles a " +
                        "  <if test='term != null and term != \"\"'>" +
                        "    LEFT JOIN article_tags at ON a.id = at.articleID " +
                        "    LEFT JOIN tags t ON at.tagID = t.id " +
                        "  </if>" +
                        "  <where> " +
                        "    <choose> " +
                        "      <when test='term != null and term != \"\"'> " +
                        "        (a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "        OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "        OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "        OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "        <if test='authorID != null and authorID != \"\"'>" +
                        "           AND  a.authorID = #{authorID}::uuid" +
                        "       </if>" +
                        "      </when> " +
                        "      <when test='authorID != null and authorID != \"\"'> " +
                        "        a.authorID = #{authorID}::uuid " +
                        "        <if test='published != null'>" +
                        "           AND  a.is_published = #{published} " +
                        "       </if>" +
                        "      </when> " +
                        "      <when test='published != null'> " +
                        "        a.is_published = #{published} " +
                        "      </when> " +
                        "    </choose> " +
                        "  </where> " +
                        "  <choose> " +
                        "    <when test='sort == \"title\"'> " +
                        "      ORDER BY a.title " +
                        "    </when> " +
                        "    <when test='sort == \"updated_at\"'> " +
                        "      ORDER BY a.updated_at DESC " +
                        "    </when> " +
                        "    <otherwise> " +
                        "      ORDER BY a.created_at DESC " +
                        "    </otherwise> " +
                        "  </choose> )" +
                        "</script>")

        Long totalArticles(@Param("term") String term, @Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("published") Boolean published);

        @Select("<script> " +
                        "SELECT * FROM articles a " +
                        "   <where> " +
                        "        a.authorID = #{authorID}::uuid " +
                        "      <if test='published != null'> " +
                        "         AND  a.is_published = #{published}" +
                        "      </if>  " +
                        "   </where> " +
                        "   <choose> " +
                        "    <when test='sort == \"title\"'> " +
                        "      ORDER BY a.title " +
                        "    </when> " +
                        "    <when test='sort == \"updated_at\"'> " +
                        "      ORDER BY a.updated_at DESC " +
                        "    </when> " +
                        "    <otherwise> " +
                        "      ORDER BY a.created_at DESC " +
                        "    </otherwise> " +
                        "</choose>  " +
                        "LIMIT #{limit} OFFSET #{start}" +
                        "</script>"

        )
        List<Article> findAuthorsArticles(@Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("limit") Long limit, @Param("start") Long start, @Param("published") Boolean published);

        @Select("INSERT INTO articles (title, authorID, content, is_published, description) VALUES (#{title}, #{authorID}::uuid, #{content}, #{is_published}, #{description}) RETURNING *")
        Article addArticle(Article article);

        @Select("<script> " +
                        "SELECT DISTINCT a.* " +
                        "FROM articles a " +
                        "LEFT JOIN article_tags at ON a.id = at.articleID " +
                        "LEFT JOIN tags t ON at.tagID = t.id " +
                        "<where> " +
                        "  (a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "  OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "  OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "  OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "  <if test='authorID != null and authorID != \"\"'> " +
                        "    AND a.authorID = #{authorID}::uuid " +
                        "  </if> " +
                        "</where> " +
                        "<choose> " +
                        "  <when test='sort == \"title\"'> " +
                        "    ORDER BY a.title " +
                        "  </when> " +
                        "  <when test='sort == \"updated_at\"'> " +
                        "    ORDER BY a.updated_at DESC " +
                        "  </when> " +
                        "  <otherwise> " +
                        "    ORDER BY a.updated_at DESC " +
                        "  </otherwise> " +
                        "</choose> " +
                        "LIMIT #{limit} OFFSET #{start}" +
                        "</script>")

        List<Article> search(@Param("term") String term, @Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("limit") Long limit, @Param("start") Long start);

        @Insert("UPDATE articles SET title = #{title}, content = #{content}, is_published = #{is_published}, description = #{description}, updated_at = #{updated_at} WHERE id = #{id}::uuid")
        void editArticle(Article article);

        @Delete("DELETE FROM articles WHERE id = #{id}::uuid")
        void deleteArticle(@Param("id") String id);

}
