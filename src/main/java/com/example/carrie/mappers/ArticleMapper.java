package com.example.carrie.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Article;

@Mapper
public interface ArticleMapper {

        @Select("SELECT * FROM articles WHERE title =#{title}")
        List<Article> findByTitle(@Param("title") String title);

        @Select("SELECT " +
                  "a.*, " +
                  "(SELECT " +
                     "COUNT(cm.*) AS totalComments " +
                  "FROM " +
                     "comments cm " +
                  "WHERE " +
                     "cm.articleID = a.id), " +
                  "SUM(cl.likes) AS totalLikes, " +
                  "SUM(cl.dislikes) AS totalDislikes " +
                "FROM " +
                    "articles a " +
                "LEFT JOIN " +
                   "claps cl ON cl.articleID = a.id " +
                "WHERE " +
                   "a.id = #{id}::uuid " +
                "GROUP BY " +
                   "a.id"
        )
        Optional<Article> findById(@Param("id") String id);

        @Select("<script> " +
                "SELECT a.*, " +
                   "(SELECT COUNT(cm.*) AS totalComments " +
                   "FROM comments cm " +
                   "WHERE cm.articleID = a.id), " +
                   "SUM(cl.likes) AS totalLikes, " +
                   "SUM(cl.dislikes) AS totalDislikes "  +
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
                @Param("endDate") LocalDateTime endDate
        );

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
                @Param("endDate") LocalDateTime endDate
        );

        @Select("<script> " +
                "SELECT a.*, " +
                  "(SELECT COUNT(cm.*) AS totalComments " +
                  "FROM comments cm " +
                  "WHERE cm.articleID = a.id), " +
                  "SUM(cl.likes) AS totalLikes, " +
                  "SUM(cl.dislikes) AS totalDislikes " +
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
                @Param("endDate") LocalDateTime endDate
        );

        @Select("<script> " +
                "SELECT " +
                  "DISTINCT a.*, " +
                  "(SELECT " +
                     "COUNT(cm.*) AS totalComments " +
                  "FROM " +
                    "comments cm " +
                  "WHERE " +
                    "cm.articleID = a.id), " +
                  "SUM(cl.likes) AS totalLikes, " +
                  "SUM(cl.dislikes) AS totalDislikes " +
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
                @Param("endDate") LocalDateTime endDate
        );

        @Select("INSERT INTO articles (title, authorID, content, status, description) VALUES (#{title}, #{authorID}::uuid, #{content}, #{status}, #{description}) RETURNING *")
        Article addArticle(Article article);

        @Update("UPDATE articles SET title = #{title}, content = #{content}, status = #{status}, description = #{description}, updatedAt = #{updatedAt} WHERE id = #{id}::uuid")
        void editArticle(Article article);

        @Delete("DELETE FROM articles WHERE id = #{id}::uuid")
        void deleteArticle(@Param("id") String id);

}
