package com.example.carrie.mappers;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.*;

import com.example.carrie.entities.Article;

@Mapper
public interface ArticleMapper {

        @Select("SELECT * FROM articles WHERE title =#{title}")
        List<Article> findByTitle(@Param("title") String title);

        @Select("SELECT a.*, (SELECT COUNT(*) FROM comments WHERE comments.articleID = a.id) AS totalComments, (SELECT SUM(claps.count) FROM claps WHERE claps.articleID = a.id) AS totalClaps FROM articles a WHERE id = #{id}::uuid")
        Optional<Article> findById(@Param("id") String id);

        @Select("<script> " +
                        "SELECT a.*, " +
                        "(SELECT COUNT(*) FROM comments WHERE comments.articleID = a.id) AS totalComments, " +
                        "(SELECT SUM(claps.count) FROM claps WHERE claps.articleID = a.id) AS totalClaps " +
                        "FROM articles a" +
                        "<if test='published != null and published != \"\"'>" +
                        "  WHERE a.isPublished = #{published}" +
                        "</if>" +
                        "<choose> " +
                        "  <when test='sort == \"title\"'> " +
                        "    ORDER BY a.title " +
                        "  </when> " +
                        "  <when test='sort == \"updatedAt\"'> " +
                        "    ORDER BY a.updatedAt DESC " +
                        "  </when> " +
                        "  <otherwise> " +
                        "    ORDER BY a.createdAt DESC " +
                        "  </otherwise> " +
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
                        "           AND  a.isPublished = #{published} " +
                        "       </if>" +
                        "      </when> " +
                        "      <when test='published != null'> " +
                        "        a.isPublished = #{published} " +
                        "      </when> " +
                        "    </choose> " +
                        "  </where> " +
                        " )" +
                        "</script>")

        Long totalArticles(@Param("term") String term, @Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("published") Boolean published);

        @Select("<script> " +
                        "SELECT a.*, " +
                        "(SELECT COUNT(*) FROM comments WHERE comments.articleID = a.id) AS totalComments, " +
                        "(SELECT SUM(claps.count) FROM claps WHERE claps.articleID = a.id) AS totalClaps " +
                        "FROM articles a" +
                        "   <where> " +
                        "        a.authorID = #{authorID}::uuid " +
                        "      <if test='published != null'> " +
                        "         AND  a.isPublished = #{published}" +
                        "      </if>  " +
                        "   </where> " +
                        "   <choose> " +
                        "    <when test='sort == \"title\"'> " +
                        "      ORDER BY a.title " +
                        "    </when> " +
                        "    <when test='sort == \"updatedAt\"'> " +
                        "      ORDER BY a.updatedAt DESC " +
                        "    </when> " +
                        "    <otherwise> " +
                        "      ORDER BY a.createdAt DESC " +
                        "    </otherwise> " +
                        "</choose>  " +
                        "LIMIT #{limit} OFFSET #{start}" +
                        "</script>"

        )
        List<Article> findAuthorsArticles(@Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("limit") Long limit, @Param("start") Long start, @Param("published") Boolean published);

        @Select("INSERT INTO articles (title, authorID, content, isPublished, description) VALUES (#{title}, #{authorID}::uuid, #{content}, #{isPublished}, #{description}) RETURNING *")
        Article addArticle(Article article);

        @Select("<script> " +
                        "SELECT DISTINCT a.*, " +
                        "(SELECT COUNT(*) FROM comments WHERE comments.articleID = a.id) AS totalComments, " +
                        "(SELECT SUM(claps.count) FROM claps WHERE claps.articleID = a.id) AS totalClaps " +
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
                        "  <when test='sort == \"updatedAt\"'> " +
                        "    ORDER BY a.updatedAt DESC " +
                        "  </when> " +
                        "  <otherwise> " +
                        "    ORDER BY a.updatedAt DESC " +
                        "  </otherwise> " +
                        "</choose> " +
                        "LIMIT #{limit} OFFSET #{start}" +
                        "</script>")

        List<Article> search(@Param("term") String term, @Param("authorID") String authorID, @Param("sort") String sort,
                        @Param("limit") Long limit, @Param("start") Long start);

        @Update("UPDATE articles SET title = #{title}, content = #{content}, isPublished = #{isPublished}, description = #{description}, updatedAt = #{updatedAt} WHERE id = #{id}::uuid")
        void editArticle(Article article);

        @Delete("DELETE FROM articles WHERE id = #{id}::uuid")
        void deleteArticle(@Param("id") String id);

}
