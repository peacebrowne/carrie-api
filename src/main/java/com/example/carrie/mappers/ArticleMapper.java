package com.example.carrie.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.carrie.dto.DailyStatsDto;
import com.example.carrie.dto.ReadingList;
import com.example.carrie.models.ReadingHistory;
import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Article;

@Mapper
public interface ArticleMapper {

        @Select("SELECT EXISTS (SELECT 1 FROM articles WHERE LOWER(title) = LOWER(#{title}) AND id != #{id}::uuid)")
        boolean existsByTitleIgnoreCase(@Param("title") String title, @Param("id") String id);

        @Select("SELECT a.*, " +
                "  (SELECT COUNT(*) FROM comments cm WHERE cm.articleID = a.id) AS totalComments, " +
                "  COALESCE(SUM(cl.likes),0) AS likes, " +
                "  COALESCE(SUM(cl.dislikes),0) AS dislikes " +
                "FROM articles a " +
                "LEFT JOIN claps cl ON cl.articleID = a.id " +
                "WHERE LOWER(REGEXP_REPLACE(a.title, '[^a-zA-Z0-9]+', ' ', 'g')) ILIKE '%' || LOWER(#{title}) || '%' " +
                "AND a.is_trash = false " +
                "GROUP BY a.id")
        Article findByTitle(@Param("title") String title);
        @Select("WITH tag_articles AS (\n" +
                "  SELECT articleId\n" +
                "  FROM article_tags\n" +
                "  WHERE tagId = #{tagId}::uuid\n" +
                ")\n" +
                "\n" +
                "SELECT a.*, COALESCE(cl.likes, 0) AS likes, COALESCE(cm.totalComments, 0) AS totalComments\n" +
                "FROM tag_articles ta\n" +
                "LEFT JOIN articles a ON a.id = ta.articleId\n" +
                "LEFT JOIN (\n" +
                "  SELECT articleId, SUM(likes) AS likes\n" +
                "  FROM claps\n" +
                "  GROUP BY articleId\n" +
                ") cl ON cl.articleId = a.id\n" +
                "LEFT JOIN (\n" +
                "  SELECT articleId, COUNT(id) AS totalComments\n" +
                "  FROM comments WHERE articleId IN (\n" +
                "    SELECT * FROM tag_articles\n" +
                "  )\n" +
                "  GROUP BY articleId\n" +
                ")cm ON cm.articleId = a.id\n" +
                "WHERE a.authorId <> #{authorId}::uuid AND a.is_trash = false AND a.status = 'published'\n" +
                "ORDER BY cl.likes\n" +
                "LIMIT #{limit} OFFSET #{start}"
                )
        List<Article> findByTag(@Param("tagId") String tagId, @Param("authorId") String authorId,
                                @Param("limit") Long limit, @Param("start") Long start);

        @Select("SELECT COUNT(*) AS total FROM (\n" +
                "  WITH tag_articles AS (\n" +
                "    SELECT articleId FROM article_tags WHERE tagId = #{tagId}::uuid\n" +
                "  )                               \n" +
                "  SELECT a.*, COALESCE(cl.likes, 0) AS likes, COALESCE(cm.totalComments, 0) AS totalComments  \n" +
                "  FROM tag_articles ta  \n" +
                "  LEFT JOIN articles a ON a.id = ta.articleId  \n" +
                "  LEFT JOIN (  \n" +
                "    SELECT articleId, SUM(likes) AS likes  \n" +
                "    FROM claps  \n" +
                "    GROUP BY articleId  \n" +
                "  ) cl ON cl.articleId = a.id  \n" +
                "  LEFT JOIN (  \n" +
                "    SELECT articleId, COUNT(id) AS totalComments  \n" +
                "    FROM comments WHERE articleId IN (  \n" +
                "      SELECT * FROM tag_articles  \n" +
                "    )  \n" +
                "    GROUP BY articleId  \n" +
                "  )cm ON cm.articleId = a.id  \n" +
                "  WHERE a.authorId <> #{authorId}::uuid AND a.is_trash = false AND a.status = 'published'  \n" +
                "  ORDER BY cl.likes \n" +
                ")")
        Long totalTagArticles(@Param("tagId") String tagId, @Param("authorId") String authorId);

        @Select("SELECT a.*, (SELECT COUNT(*) FROM comments cm WHERE cm.articleID = a.id) AS totalComments, COALESCE(SUM(cl.likes),0) AS likes, COALESCE(SUM(cl.dislikes),0) AS dislikes FROM articles a LEFT JOIN claps cl ON cl.articleID = a.id WHERE a.id = #{id}::uuid AND a.is_trash = false GROUP BY a.id")
        Optional<Article> findById(@Param("id") String id);

        @Select("<script> " +
                        "SELECT a.*, " +
                        "COALESCE(COUNT(DISTINCT cm.id), 0) AS total_comments, " +
                        "COALESCE(SUM(cl.likes), 0) AS likes, " +
                        "COALESCE(SUM(cl.dislikes), 0) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "LEFT JOIN comments cm ON cm.articleID = a.id " +
                        "WHERE a.is_trash = false " +
                        "<choose> " +
                        "<when test='status != null and status != \"\"'>" +
                        "AND a.status = #{status} " +
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

        @Select("<script>" +
                        "SELECT COUNT(*) AS total FROM (" +
                        "   SELECT a.id " +
                        "   FROM articles a " +
                        "   LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "   LEFT JOIN comments cm ON cm.articleID = a.id " +
                        "   <where> " +
                        "     a.authorID = #{authorID}::uuid " +
                        "     <if test='status != null'> AND a.status = #{status} </if> " +
                        "     <choose> " +
                        "       <when test='startDate != null and endDate != null'> AND a.createdAt BETWEEN #{startDate} AND #{endDate} </when> "
                        +
                        "       <when test='startDate != null'> AND a.createdAt &gt;= #{startDate} </when> " +
                        "       <when test='endDate != null'> AND a.createdAt &lt;= #{endDate} </when> " +
                        "     </choose> " +
                        "   </where> " +
                        "   GROUP BY a.id " +
                        ") sub" +
                        "</script>")
        Long totalAuthorArticles(
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script>" +
                        "SELECT a.*, " +
                        "COALESCE(COUNT(DISTINCT cm.id), 0) AS totalComments, " +
                        "COALESCE(SUM(cl.likes), 0) AS likes, " +
                        "COALESCE(SUM(cl.dislikes), 0) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "LEFT JOIN comments cm ON cm.articleID = a.id " +
                        "<where> " +
                        "  a.authorID = #{authorID}::uuid " +
                        "  <if test='status != null'> AND a.status = #{status} </if> " +
                        "  <choose> " +
                        "    <when test='startDate != null and endDate != null'> AND a.createdAt BETWEEN #{startDate} AND #{endDate} </when> "
                        +
                        "    <when test='startDate != null'> AND a.createdAt &gt;= #{startDate} </when> " +
                        "    <when test='endDate != null'> AND a.createdAt &lt;= #{endDate} </when> " +
                        "  </choose> " +
                        "</where> " +
                        "GROUP BY a.id " +
                        "<choose> " +
                        "  <when test='sort == \"title\"'> ORDER BY a.title </when> " +
                        "  <when test='sort == \"updatedAt\"'> ORDER BY a.updatedAt </when> " +
                        "  <otherwise> ORDER BY a.createdAt </otherwise> " +
                        "</choose> " +
                        "LIMIT #{limit} OFFSET #{start} " +
                        "</script>")
        List<Article> findAuthorsArticles(
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("limit") Long limit,
                        @Param("start") Long start,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script>" +
                        "SELECT COUNT(*) AS total FROM (" +
                        "   SELECT a.id " +
                        "   FROM articles a " +
                        "   LEFT JOIN comments cm ON cm.articleID = a.id " +
                        "   LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "   LEFT JOIN article_tags at ON a.id = at.articleID " +
                        "   LEFT JOIN tags t ON at.tagID = t.id " +
                        "   <where> " +
                        "      (a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "       OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "       OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "       OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "      <if test='authorID != null and authorID != \"\"'> " +
                        "         AND a.authorID = #{authorID}::uuid " +
                        "      </if> " +
                        "      <if test='status != null and status != \"\"'> " +
                        "         AND a.status = #{status} " +
                        "      </if> " +
                        "      <choose> " +
                        "         <when test='startDate != null and endDate != null'> " +
                        "            AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "         </when> " +
                        "         <when test='startDate != null'> " +
                        "            AND a.createdAt &gt;= #{startDate} " +
                        "         </when> " +
                        "         <when test='endDate != null'> " +
                        "            AND a.createdAt &lt;= #{endDate} " +
                        "         </when> " +
                        "      </choose> " +
                        "   </where> " +
                        "   GROUP BY a.id " +
                        ") sub" +
                        "</script>")
        Long totalSearchArticles(
                        @Param("term") String term,
                        @Param("authorID") String authorID,
                        @Param("sort") String sort,
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Select("<script>" +
                        "SELECT DISTINCT a.*, " +
                        "       COUNT(DISTINCT cm.id) AS totalComments, " +
                        "       COALESCE(SUM(cl.likes), 0) AS likes, " +
                        "       COALESCE(SUM(cl.dislikes), 0) AS dislikes " +
                        "FROM articles a " +
                        "LEFT JOIN comments cm ON cm.articleID = a.id " +
                        "LEFT JOIN claps cl ON cl.articleID = a.id " +
                        "LEFT JOIN article_tags at ON a.id = at.articleID " +
                        "LEFT JOIN tags t ON at.tagID = t.id " +
                        "<where> " +
                        "   (a.title ILIKE CONCAT('%', #{term}, '%') " +
                        "    OR a.content ILIKE CONCAT('%', #{term}, '%') " +
                        "    OR a.description ILIKE CONCAT('%', #{term}, '%') " +
                        "    OR t.name ILIKE CONCAT('%', #{term}, '%')) " +
                        "   <if test='authorID != null and authorID != \"\"'> " +
                        "      AND a.authorID = #{authorID}::uuid " +
                        "   </if> " +
                        "   <if test='status != null and status != \"\"'> " +
                        "      AND a.status = #{status} " +
                        "   </if> " +
                        "   <choose> " +
                        "      <when test='startDate != null and endDate != null'> " +
                        "         AND a.createdAt BETWEEN #{startDate} AND #{endDate} " +
                        "      </when> " +
                        "      <when test='startDate != null'> " +
                        "         AND a.createdAt &gt;= #{startDate} " +
                        "      </when> " +
                        "      <when test='endDate != null'> " +
                        "         AND a.createdAt &lt;= #{endDate} " +
                        "      </when> " +
                        "   </choose> " +
                        "</where> " +
                        "GROUP BY a.id " +
                        "<choose> " +
                        "   <when test='sort == \"title\"'> ORDER BY a.title </when> " +
                        "   <when test='sort == \"updatedAt\"'> ORDER BY a.updatedAt DESC </when> " +
                        "   <otherwise> ORDER BY a.createdAt DESC </otherwise> " +
                        "</choose> " +
                        "LIMIT #{limit} OFFSET #{start} " +
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

        @Select("INSERT INTO article_shares (article_id, shared_by) VALUES (#{articleId}::uuid, #{sharedBy}::uuid) RETURNING *")
        Map<String, Object> shareArticle(@Param("articleId") String articleId,
                        @Param("sharedBy") String sharedBy);

        @Select("SELECT * FROM article_shares WHERE article_id = #{articleId}::uuid")
        List<Map<String, Object>> getSharesByArticle(@Param("articleId") String articleId);

        @Select("SELECT DISTINCT ON (ar.title) ar.*, t.name FROM articles ar INNER JOIN article_tags art ON art.articleId = ar.id INNER JOIN tags t ON t.id = art.tagID INNER JOIN author_interest ai ON t.id = ai.tag_id WHERE ai.author_id = #{authorId}::uuid AND ar.status = 'published' ORDER BY ar.title, ar.createdAt DESC LIMIT #{limit} OFFSET #{start}")
        List<Article> findArticlesByAuthorInterest(@Param("authorId") String authorId, @Param("limit") Long limit,
                        @Param("start") Long start);

        @Select("SELECT \n" +
                "a.*,\n" +
                "\n" +
                "  (SELECT COUNT(*) FROM comments cm WHERE cm.articleID = a.id) AS totalComments,\n" +
                "\n" +
                "  COALESCE(cl.likes, 0) AS likes,\n" +
                "  COALESCE(cl.dislikes, 0) AS dislikes\n" +
                "\n" +
                "FROM articles a\n" +
                "\n" +
                "LEFT JOIN (\n" +
                "    SELECT articleID, \n" +
                "           SUM(likes) AS likes, \n" +
                "           SUM(dislikes) AS dislikes\n" +
                "    FROM claps\n" +
                "    GROUP BY articleID\n" +
                ") cl ON cl.articleID = a.id\n" +
                "\n" +
                "LEFT JOIN article_tags at ON at.articleId = a.id \n" +
                "LEFT JOIN author_interest ai ON ai.tagID = at.tagID \n" +
                "\n" +
                "WHERE \n" +
                "    ai.authorId = #{authorId}::uuid\n" +
                "    AND a.status = 'published'\n" +
                "    AND a.is_trash = false\n" +
                "\n" +
                "GROUP BY a.id, cl.likes, cl.dislikes\n" +
                "ORDER BY a.title\n" +
                "LIMIT #{limit} OFFSET #{start};")
        List<Article> findAuthorInterestedArticles(@Param("authorId") String authorID, @Param("limit") Long limit,
                        @Param("start") Long start);

        @Select("SELECT COUNT(*) AS total FROM (SELECT a.title FROM articles a LEFT JOIN article_tags at ON at.articleId = a.id LEFT JOIN author_interest ai ON ai.tagID = at.tagID WHERE ai.authorId = #{authorId}::uuid AND a.status = 'published' ORDER BY a.title)")
        Long totalAuthorInterestArticles(@Param("authorId") String authorID);

        @Select("INSERT INTO reading_list(authorId, articleId) VALUES(#{authorId}::uuid, #{articleId}::uuid) RETURNING *")
        ReadingList addToReadingList(@Param("articleId") String articleId, @Param("authorId") String authorId);

        @Select("SELECT * FROM reading_list WHERE id = #{id}::uuid")
        ReadingList findReadingListById(String id);

        @Select("SELECT * FROM reading_list WHERE authorId = #{authorId}::uuid ORDER BY savedAt DESC LIMIT 5 OFFSET 0")
        List<ReadingList> findUserReadingList(@Param("authorId") String authorId);

        @Select("SELECT * FROM reading_list WHERE authorId = #{authorId}::uuid AND articleId = #{articleId}::uuid")
        ReadingList getList(@Param("authorId") String authorId, @Param("articleId") String articleId);

        @Select("SELECT COUNT(*) FROM reading_list WHERE authorId = #{authorId}::uuid")
        Long totalUserReadingList(@Param("authorId") String authorId);

        @Select("DELETE FROM reading_list WHERE authorId = #{authorId}::uuid AND articleId = #{articleId}::uuid RETURNING *")
        ReadingList removeFromReadingList(@Param("authorId") String authorId, @Param("articleId") String articleId);

        @Update("UPDATE articles SET status = 'published' WHERE id = #{id}::uuid")
        void publishArticle(String id);

        @Update("UPDATE articles SET status = 'scheduled', publish_date = #{dateTime} WHERE id = #{articleId}::uuid")
        void scheduledArticle(String articleId, LocalDateTime dateTime);

        @Select("INSERT INTO reading_history " +
                "(userID, articleID, readAt, timeSpentSeconds) " +
                "VALUES " +
                "(#{userId}, #{articleId}, #{readAt} RETURNING *)")
        ReadingHistory addReadHistory(
                @Param("userId") String userId,
                @Param("articleId") String articleId
        );

        @Update("UPDATE reading_history " +
                "SET timeSpentSeconds = timeSpentSeconds + #{timeSpent} " +
                "WHERE user_id = #{userId} AND article_id = #{articleId}")
        int updateTimeSpent(
                @Param("userId") String userId,
                @Param("articleId") String articleId,
                @Param("timeSpent") Integer timeSpent
        );

        @Select("SELECT\n" +
                "  a.*,\n" +
                "  COALESCE(cl.likes, 0) AS likes,\n" +
                "  (\n" +
                "    COUNT(DISTINCT at.tagid) * 3\n" +
                "    + COALESCE(cl.likes, 0) * 0.2\n" +
                "    + CASE\n" +
                "        WHEN a.createdAt >= NOW() - INTERVAL '1 day' THEN 5\n" +
                "        WHEN a.createdAt >= NOW() - INTERVAL '3 days' THEN 3\n" +
                "        WHEN a.createdAt >= NOW() - INTERVAL '7 days' THEN 1\n" +
                "        ELSE 0\n" +
                "      END\n" +
                "  ) AS score\n" +
                "FROM articles a\n" +
                "INNER JOIN article_tags at ON at.articleid = a.id\n" +
                "INNER JOIN author_interest ai ON ai.tagid = at.tagid AND ai.authorId = #{userId}::uuid\n" +
                "LEFT JOIN reading_history h ON h.articleId = a.id AND h.userId = #{userId}::uuid\n" +
                "LEFT JOIN (\n" +
                "  SELECT articleid, SUM(likes) AS likes\n" +
                "  FROM claps\n" +
                "  GROUP BY articleid\n" +
                ") cl ON cl.articleid = a.id\n" +
                "WHERE a.status = 'published'\n" +
                "  AND a.is_trash = false\n" +
                "  AND h.articleId IS NULL\n" +
                "  AND a.createdAt >= NOW() - INTERVAL '1 YEAR'\n" +
                "GROUP BY a.id, a.title, a.createdAt, a.authorId, cl.likes\n" +
                "ORDER BY score DESC\n" +
                "LIMIT #{limit} OFFSET #{start};")
        List<Article> findUserPersonalizedFeeds(@Param("userId") String userId,
                                                @Param("limit") Long limit,
                                                @Param("start") Long start);

        @Select("SELECT \n" +
                "  COUNT(*) AS total \n" +
                "  FROM (\n" +
                "      SELECT \n" +
                "        a.id,  \n" +
                "        a.title,  \n" +
                "        a.description, \n" +
                "        a.authorid AS authorId,   \n" +
                "        a.publish_date,\n" +
                "        COUNT(at.tagid) AS interest_match_count  \n" +
                "      FROM  articles a INNER JOIN article_tags at ON at.articleid = a.id\n" +
                "      INNER JOIN author_interest ai ON ai.tagid = at.tagid\n" +
                "      AND ai.authorId = #{userId}::uuid\n" +
                "      LEFT JOIN reading_history h ON h.articleId = a.id\n" +
                "      AND h.userId = #{userId}::uuid WHERE a.status = 'published'\n" +
                "      AND a.is_trash = false\n" +
                "      AND h.articleId IS NULL\n" +
                "      GROUP BY \n" +
                "        a.id, \n" +
                "        a.title, \n" +
                "        a.description, \n" +
                "        a.authorid, \n" +
                "        a.publish_date \n" +
                "      ORDER BY interest_match_count DESC,\n" +
                "        a.publish_date DESC\n" +
                ");")
        Long totalFindUserPersonalizedFeeds(@Param("userId") String userId);

        @Select("SELECT\n" +
                "  a.*,\n" +
                "  COALESCE(c.likes, 0) AS likes,\n" +
                "  COALESCE(c.dislikes, 0) AS dislikes\n" +
                "FROM articles a\n" +
                "JOIN claps c ON c.articleid = a.id\n" +
                "WHERE a.createdAt >= NOW() - INTERVAL '7 days'\n" +
                "GROUP BY a.id, a.title, c.likes, c.dislikes\n" +
                "ORDER BY likes DESC\n" +
                "LIMIT 10;")
        List<Article>findTrendingArticles();

    @Select("SELECT\n" +
            "  a.*,\n" +
            "  COALESCE(c.likes, 0) AS likes,\n" +
            "  COALESCE(c.dislikes, 0) AS dislikes\n" +
            "FROM articles a\n" +
            "JOIN claps c ON c.articleid = a.id\n" +
            "JOIN article_tags art ON art.articleid = a.id\n" +
            "WHERE art.tagid = #{tagId}::uuid\n" +
            "AND a.createdAt >= NOW() - INTERVAL '7 days'\n" +
            "GROUP BY a.id, a.title, c.likes, c.dislikes\n" +
            "ORDER BY likes DESC\n" +
            "LIMIT 10;")
    List<Article>findLatestTagArticles(@Param("tagId") String tagId);

        @Insert("INSERT INTO article_views (articleId, userId) VALUES (#{articleId}::uuid, #{userId}::uuid)")
        void insertArticleView(@Param("articleId") String articleId, @Param("userId") String userId);

        @Insert("INSERT INTO article_reads (articleId, userId) VALUES (#{articleId}::uuid, #{userId}::uuid)")
        void insertArticleRead(@Param("articleId") String articleId, @Param("userId") String userId);

        @Insert("INSERT INTO article_read_sessions (articleId, userId, duration) VALUES (#{articleId}::uuid, #{userId}::uuid, #{duration})")
        void insertReadSession(@Param("articleId") String articleId, @Param("userId") String userId,@Param("duration") int duration);

        @Select("SELECT EXISTS (SELECT 1 FROM article_views WHERE articleId = #{articleId}::uuid AND userId = #{userId}::uuid)")
        boolean isViewExist(@Param("articleId") String articleId, @Param("userId") String userId);

        @Select("SELECT EXISTS (SELECT 1 FROM article_reads WHERE articleId = #{articleId}::uuid AND userId = #{userId}::uuid)")
        boolean isReadExist(@Param("articleId") String articleId, @Param("userId") String userId);

        @Select("SELECT COUNT(*) FROM article_views WHERE articleId = #{articleId}::uuid")
        int countArticleViews(String articleId);

        @Select("SELECT COUNT(*) FROM article_reads WHERE articleId = #{articleId}::uuid")
        int countArticleReads(String articleId);

        @Select("SELECT COALESCE(AVG(duration), 0) FROM article_read_sessions  WHERE articleId = #{articleId}::uuid")
        Integer avgReadTime(String articleId);

        @Select("<script>\n" +
                "WITH target_articles AS (\n" +
                "    SELECT id FROM articles WHERE authorId = #{authorId}::uuid\n" +
                "),\n" +
                "time_windows AS (\n" +
                "    SELECT \n" +
                "        CASE \n" +
                "            WHEN #{duration} = 'this_year' THEN DATE_TRUNC('YEAR', NOW()) \n" +
                "            WHEN #{duration} IS NOT NULL AND #{duration} != '' THEN NOW() - CAST(#{duration} AS INTERVAL)\n" +
                "            ELSE '1970-01-01'::timestamp \n" +
                "        END as curr_start,\n" +
                "        CASE \n" +
                "            WHEN #{duration} = 'this_year' THEN DATE_TRUNC('YEAR', NOW()) - INTERVAL '1 year'\n" +
                "            WHEN #{duration} IS NOT NULL AND #{duration} != '' THEN NOW() - (CAST(#{duration} AS INTERVAL) * 2)\n" +
                "            ELSE '1970-01-01'::timestamp\n" +
                "        END as prev_start\n" +
                ")\n" +
                "SELECT \n" +
                "    /* CLAPS */\n" +
                "    (SELECT COALESCE(SUM(cl.likes), 0) FROM claps cl WHERE cl.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ cl.createdAt >= (SELECT curr_start FROM time_windows) ]]> ) as current_claps,\n" +
                "    (SELECT COALESCE(SUM(cl.likes), 0) FROM claps cl WHERE cl.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ cl.createdAt >= (SELECT prev_start FROM time_windows) AND cl.createdAt < (SELECT curr_start FROM time_windows) ]]> ) as previous_claps,\n" +
                "\n" +
                "    /* VIEWS */\n" +
                "    (SELECT COUNT(*) FROM article_views av WHERE av.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ av.viewedAt >= (SELECT curr_start FROM time_windows) ]]> ) as current_views,\n" +
                "    (SELECT COUNT(*) FROM article_views av WHERE av.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ av.viewedAt >= (SELECT prev_start FROM time_windows) AND av.viewedAt < (SELECT curr_start FROM time_windows) ]]> ) as previous_views,\n" +
                "\n" +
                "    /* READS */\n" +
                "    (SELECT COUNT(*) FROM article_reads ar WHERE ar.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ ar.readAt >= (SELECT curr_start FROM time_windows) ]]> ) as current_reads,\n" +
                "    (SELECT COUNT(*) FROM article_reads ar WHERE ar.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ ar.readAt >= (SELECT prev_start FROM time_windows) AND ar.readAt < (SELECT curr_start FROM time_windows) ]]> ) as previous_reads,\n" +
                "\n" +
                "    /* SESSIONS */\n" +
                "    (SELECT COUNT(*) FROM article_read_sessions ars WHERE ars.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ ars.createdAt >= (SELECT curr_start FROM time_windows) ]]> ) as current_total_sessions,\n" +
                "    (SELECT COUNT(*) FROM article_read_sessions ars WHERE ars.articleid IN (SELECT id FROM target_articles) \n" +
                "     AND <![CDATA[ ars.createdAt >= (SELECT prev_start FROM time_windows) AND ars.createdAt < (SELECT curr_start FROM time_windows) ]]> ) as previous_total_sessions\n" +
                "</script>")
        Map<String, Long> getAuthorStats(@Param("authorId") String authorId, @Param("duration") String duration);

        @Select("WITH target_articles AS (\n" +
                "    SELECT id FROM articles WHERE authorId = #{authorId}::uuid\n" +
                "),\n" +
                "time_series AS (\n" +
                "    SELECT generate_series(\n" +
                "        DATE_TRUNC('MONTH', NOW()),\n" +
                "        NOW(),\n" +
                "        INTERVAL '1 DAY'\n" +
                "    ) AS day\n" +
                ")\n" +
                "SELECT \n" +
                "    TO_CHAR(ts.day, 'YYYY-MM-DD HH24:MI:SS') as label,\n" +
                "    COALESCE(c.total_claps, 0) AS claps,\n" +
                "    COALESCE(r.total_reads, 0) AS reads,\n" +
                "    COALESCE(v.total_views, 0) AS views\n" +
                "FROM time_series ts\n" +
                "-- Join Claps\n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_TRUNC('DAY', createdAt) AS day, SUM(likes) AS total_claps\n" +
                "    FROM claps \n" +
                "    WHERE articleId IN (SELECT id FROM target_articles)\n" +
                "    GROUP BY 1\n" +
                ") c ON c.day = DATE_TRUNC('DAY', ts.day)\n" +
                "-- Join Reads\n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_TRUNC('DAY', readAt) AS day, COUNT(id) AS total_reads\n" +
                "    FROM article_reads \n" +
                "    WHERE articleId IN (SELECT id FROM target_articles)\n" +
                "    GROUP BY 1\n" +
                ") r ON r.day = DATE_TRUNC('DAY', ts.day)\n" +
                "-- Join Views\n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_TRUNC('DAY', viewedAt) AS day, COUNT(id) AS total_views\n" +
                "    FROM article_views \n" +
                "    WHERE articleId IN (SELECT id FROM target_articles)\n" +
                "    GROUP BY 1\n" +
                ") v ON v.day = DATE_TRUNC('DAY', ts.day)\n" +
                "ORDER BY ts.day;")
        List<DailyStatsDto> getAuthorDailyStats(@Param("authorId") String authorId);

        @Select("SELECT \n" +
                "  a.title,\n" +
                "  a.id,\n" +
                "  a.publish_date,\n" +
                "  (COUNT(DISTINCT av.id) * 1) +\n" +
                "  (COUNT(DISTINCT ar.id) * 3) +\n" +
                "  (SUM(COALESCE(cl.likes, 0)) * 5) AS interaction_score\n" +
                "FROM articles a \n" +
                "LEFT JOIN article_views av ON av.articleid = a.id\n" +
                "LEFT JOIN article_reads ar ON ar.articleid = a.id\n" +
                "LEFT JOIN claps cl ON cl.articleid = a.id\n" +
                "WHERE a.authorid = #{authorId}::uuid\n" +
                "GROUP BY a.id \n" +
                "ORDER BY interaction_score DESC\n" +
                "LIMIT 5;")
        List<Map<String, Long>> getAuthorBestPerformingArticles(@Param("authorId") String authorId);
}