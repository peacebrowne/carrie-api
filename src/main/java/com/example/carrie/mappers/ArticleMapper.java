package com.example.carrie.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Article;

@Mapper
public interface ArticleMapper {

        @Select("SELECT * FROM articles WHERE title =#{title}")
        List<Article> findByTitle(@Param("title") String title);

        @Select("SELECT a.* FROM articles a LEFT JOIN article_tags at ON a.id = at.articleID LEFT JOIN tags t ON t.id = at.tagID WHERE t.name ILIKE #{tag} ORDER BY a.updatedAt DESC LIMIT #{limit} OFFSET #{start}")
        List<Article> findByTag(@Param("tag") String tag, @Param("limit") Long limit,
                        @Param("start") Long start);

        @Select("SELECT COUNT(*) AS total FROM (SELECT DISTINCT a.* FROM articles a LEFT JOIN article_tags at ON a.id = at.articleID LEFT JOIN tags t ON t.id = at.tagID WHERE t.name ILIKE #{tag})")
        Long totalTagArticles(@Param("tag") String tag);

        @Select("SELECT " +
                        "a.*, " +
                        "(SELECT " +
                        "COUNT(cm.*) AS totalComments " +
                        "FROM " +
                        "comments cm " +
                        "WHERE " +
                        "cm.articleID = a.id), " +
                        "SUM(cl.likes) AS likes, " +
                        "SUM(cl.dislikes) AS dislikes " +
                        "FROM " +
                        "articles a " +
                        "LEFT JOIN " +
                        "claps cl ON cl.articleID = a.id " +
                        "WHERE " +
                        "a.id = #{id}::uuid " +
                        "GROUP BY " +
                        "a.id")
        Optional<Article> findById(@Param("id") String id);

        @Select("<script> " +
                        "SELECT a.*, " +
                        "(SELECT COUNT(cm.*) AS totalComments " +
                        "FROM comments cm " +
                        "WHERE cm.articleID = a.id), " +
                        "SUM(cl.likes) AS likes, " +
                        "SUM(cl.dislikes) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "<choose> " +
                        "<when test='status != null and status != \"\"'>" +
                        "WHERE a.status = #{status} " +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</when>" +
                        "<when test='startDate != null and endDate != null'> " +
                        "a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "GROUP BY " +
                        "a.id" +
                        "<choose> " +
                        "<when test='sort == \"title\"'> " +
                        "ORDER BY a.title " +
                        "</when> " +
                        "<when test='sort == \"updatedAt\"'> " +
                        "ORDER BY a.updatedAt DESC " +
                        "</when> " +
                        "<otherwise> " +
                        "ORDER BY a.createdAt DESC " +
                        "</otherwise> " +
                        "</choose>  " +
                        "LIMIT #{limit} " +
                        "OFFSET #{start}" +
                        "</script>"

        )
        List<Article> findAll(
                        @Param("sort") String sort,
                        @Param("limit") Long limit,
                        @Param("start") Long start,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script> " +
                        "SELECT COUNT(*) AS total FROM ( " +
                        "SELECT DISTINCT a.* " +
                        "FROM articles a " +
                        "<if test='term != null and term != \"\"'>" +
                        "LEFT JOIN article_tags at ON a.id = at.articleID " +
                        "LEFT JOIN tags t ON at.tagID = t.id " +
                        "</if>" +
                        "<where> " +
                        "<choose> " +
                        "<when test='term != null and term != \"\"'> " +
                        "(a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "<if test='authorID != null and authorID != \"\"'>" +
                        "AND  a.authorID = #{authorID}::uuid" +
                        "</if>" +
                        "<if test='status != null and status != \"\"'>" +
                        "AND  a.status = #{status} " +
                        "</if>" +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</when> " +
                        "<when test='authorID != null and authorID != \"\"'> " +
                        "a.authorID = #{authorID}::uuid " +
                        "<if test='status != null and status != \"\"'>" +
                        "AND  a.status = #{status} " +
                        "</if>" +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</when> " +
                        "<when test='status != null and status != \"\"'> " +
                        "a.status = #{status}" +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</when> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose> " +
                        "  </where> " +
                        " )" +
                        "</script>")
        Long totalArticles(
                        @Param("term") String term,
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script> " +
                        "SELECT a.*, " +
                        "(SELECT COUNT(cm.*) AS totalComments " +
                        "FROM comments cm " +
                        "WHERE cm.articleID = a.id), " +
                        "SUM(cl.likes) AS likes, " +
                        "SUM(cl.dislikes) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "<where> " +
                        "a.authorID = #{authorID}::uuid " +
                        "<if test='status != null'> " +
                        "AND  a.status = #{status}" +
                        "</if>  " +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</where> " +
                        "GROUP BY " +
                        "a.id" +
                        "<choose> " +
                        "<when test='sort == \"title\"'> " +
                        "ORDER BY a.title " +
                        "</when> " +
                        "<when test='sort == \"updatedAt\"'> " +
                        "ORDER BY a.updatedAt DESC " +
                        "</when> " +
                        "<otherwise> " +
                        "ORDER BY a.createdAt DESC " +
                        "</otherwise> " +
                        "</choose>  " +
                        "LIMIT #{limit} " +
                        "OFFSET #{start}" +
                        "</script>"

        )
        List<Article> findAuthorsArticles(
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("limit") Long limit,
                        @Param("start") Long start,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script> " +
                        "SELECT " +
                        "DISTINCT a.*, " +
                        "(SELECT " +
                        "COUNT(cm.*) AS totalComments " +
                        "FROM " +
                        "comments cm " +
                        "WHERE " +
                        "cm.articleID = a.id), " +
                        "SUM(cl.likes) AS likes, " +
                        "SUM(cl.dislikes) AS dislikes " +
                        "FROM " +
                        "articles a " +
                        "LEFT JOIN " +
                        "claps cl ON cl.articleID = a.id " +
                        "LEFT JOIN " +
                        "article_tags at ON a.id = at.articleID " +
                        "LEFT JOIN " +
                        "tags t ON at.tagID = t.id " +
                        "<where> " +
                        "(a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "<if test='authorID != null and authorID != \"\"'> " +
                        "AND a.authorID = #{authorID}::uuid " +
                        "</if> " +
                        "<if test='status != null and status != \"\"'> " +
                        "AND  a.status = #{status}" +
                        "</if>  " +
                        "<choose> " +
                        "<when test='startDate != null and endDate != null'> " +
                        "AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "</when> " +
                        "<when test='startDate != null'> " +
                        "AND a.createdAt &gt;= #{startDate} " +
                        "</when> " +
                        "<when test='endDate != null'> " +
                        "AND a.createdAt &lt;= #{endDate} " +
                        "</when> " +
                        "</choose>  " +
                        "</where> " +
                        "GROUP BY " +
                        "a.id" +
                        "<choose> " +
                        "<when test='sort == \"title\"'> " +
                        "ORDER BY a.title " +
                        "</when> " +
                        "<when test='sort == \"updatedAt\"'> " +
                        "ORDER BY a.updatedAt DESC " +
                        "</when> " +
                        "<otherwise> " +
                        "ORDER BY a.updatedAt DESC " +
                        "</otherwise> " +
                        "</choose> " +
                        "LIMIT #{limit} " +
                        "OFFSET #{start}" +
                        "</script>")
        List<Article> search(
                        @Param("term") String term,
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("limit") Long limit,
                        @Param("start") Long start,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("INSERT INTO articles (title, authorID, content, status, description) VALUES (#{title}, #{authorID}::uuid, #{content}, #{status}, #{description}) RETURNING *")
        Article addArticle(Article article);

        @Update("UPDATE articles SET title = #{title}, content = #{content}, status = #{status}, description = #{description}, updatedAt = #{updatedAt} WHERE id = #{id}::uuid")
        void editArticle(Article article);

        @Delete("DELETE FROM articles WHERE id = #{id}::uuid")
        void deleteArticle(@Param("id") String id);

        @Select("SELECT " +
                        "COUNT(a.*) AS total, " +
                        "COUNT(CASE WHEN a.status = 'pending' THEN 1 END) AS pending, " +
                        "COUNT(CASE WHEN a.status = 'published' THEN 1 END) AS published, " +
                        "COUNT(CASE WHEN a.status = 'draft' THEN 1 END) AS draft, " +
                        "COALESCE(SUM(cl.likes), 0) AS likes, " +
                        "COALESCE(SUM(cl.dislikes), 0) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN claps cl ON cl.articleId = a.id " +
                        "LEFT JOIN authors ath ON ath.id = a.authorId " +
                        "WHERE a.authorId = #{authorId}::uuid")
        Map<String, Object> getTotalArticleAnalytics(@Param("authorId") String authorId);

        @Select("INSERT INTO article_shares (article_id, shared_by) VALUES (#{articleId}::uuid, #{sharedBy}::uuid) RETURNING *")
        Map<String, Object> shareArticle(@Param("articleId") String articleId,
                        @Param("sharedBy") String sharedBy);

        @Select("SELECT * FROM article_shares WHERE article_id = #{articleId}::uuid")
        List<Map<String, Object>> getSharesByArticle(@Param("articleId") String articleId);

        @Select("SELECT DISTINCT(ar.title) ar.*, t.name " +
                        "FROM articles ar " +
                        "INNER JOIN article_tags art ON art.articleId = ar.id " +
                        "INNER JOIN tags t ON t.id = art.tagID " +
                        "INNER JOIN author_interest ai ON t.id = ai.tag_id " +
                        "WHERE ai.author_id = #{authorId}::uuid AND ar.status = 'published " +
                        "ORDER BY ar.title, ar.createdAt DESC " +
                        "LIMIT #{limit} OFFSET #{start}")
        List<Article> findArticlesByAuthorInterest(@Param("authorId") String authorId, @Param("limit") Long limit,
                        @Param("start") Long start);

        @Select("SELECT a.*, (SELECT COUNT(cm.*) AS totalComments FROM comments cm WHERE cm.articleID = a.id), COALESCE(SUM(cl.likes), 0) AS likes, COALESCE(SUM(cl.dislikes), 0) AS dislikes FROM articles a LEFT JOIN claps cl ON cl.articleID = a.id LEFT JOIN article_tags at ON at.articleId = a.id LEFT JOIN author_interest ai ON ai.tagID = at.tagID WHERE ai.authorId = #{authorId}::uuid AND a.status = 'published' GROUP BY a.id ORDER BY a.title LIMIT #{limit} OFFSET #{start}")
        List<Article> findAuthorInterestedArticles(@Param("authorId") String authorID, @Param("limit") Long limit,
                        @Param("start") Long start);

        @Select("SELECT COUNT(*) AS total FROM (SELECT a.title FROM articles a LEFT JOIN article_tags at ON at.articleId = a.id LEFT JOIN author_interest ai ON ai.tagID = at.tagID WHERE ai.authorId = #{authorId}::uuid AND a.status = 'published' ORDER BY a.title)")
        Long totalAuthorInterestArticles(@Param("authorId") String authorID);

}