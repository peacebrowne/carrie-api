package com.example.carrie.mappers;

import com.example.carrie.dto.AuthorDto;
import com.example.carrie.models.Article;
import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AuthorMapper {

    @Select("SELECT * FROM authors ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Author> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT a.* FROM authors a WHERE a.id = #{id}::uuid")
    Author findById(@Param("id") String id);

    @Select("SELECT * FROM authors WHERE email = #{target} OR username = #{target}")
    Optional<Author> findByEmailOrUsername(@Param("target") String target);

    @Select("SELECT username, password FROM authors WHERE email = #{target} OR username = #{target}")
    Login findLoginDetails(@Param("target") String target);

    @Select("INSERT INTO authors (username, email, dob, gender, firstName, lastName, password, address, msisdn, biography) VALUES (#{username}, #{email}, #{dob}, #{gender}, #{firstName}, #{lastName}, #{password}, #{address}, #{msisdn}, #{biography}) RETURNING *")
    Author addAuthor(Author author);

    @Update("UPDATE authors SET username = #{username}, email = #{email}, dob = #{dob},  gender = #{gender}, firstName = #{firstName}, lastName = #{lastName}, address = #{address}, msisdn = #{msisdn}, biography = #{biography} WHERE id = #{id}::uuid")
    void editAuthor(AuthorDto author);

    @Delete("DELETE FROM authors WHERE id = #{id}::uuid")
    void deleteAuthor(@Param("id") String id);

    @Select("INSERT INTO author_followers (follower, author) VALUES (#{follower}::uuid ,#{author}::uuid) RETURNING *")
    AuthorDto followAuthor(@Param("follower") String follower, @Param("author") String author);

    @Select("DELETE FROM author_followers WHERE follower = #{follower}::uuid AND author = #{author}::uuid RETURNING *")
    AuthorDto unfollowAuthor(@Param("follower") String follower, @Param("author") String author);

    @Select("SELECT * FROM author_followers WHERE follower = #{follower}::uuid AND author = #{author}::uuid")
    AuthorDto getSingleAuthorFollower(@Param("follower") String follower, @Param("author") String author);

    @Select("SELECT a.* FROM authors a LEFT JOIN author_followers af ON a.id = af.follower WHERE af.author = #{id}::uuid LIMIT #{limit} OFFSET #{start}")
    List<AuthorDto> getAuthorFollowers(@Param("id") String id, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT COUNT(*) AS total FROM (SELECT a.id FROM authors a LEFT JOIN author_followers af ON a.id = af.follower WHERE af.author = #{id}::uuid)")
    Long totalAuthorFollower(@Param("id") String id);

    @Select("SELECT a.* FROM author_followers af LEFT JOIN authors a ON a.id = af.author WHERE af.follower = #{id}::uuid LIMIT #{limit} OFFSET #{start}")
    List<AuthorDto> getFollowedAuthors(@Param("id") String id, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT COUNT(*) AS total FROM (SELECT a.id FROM authors a LEFT JOIN author_followers af ON a.id = af.follower WHERE af.author = #{id}::uuid)")
    Long totalFollowedAuthors(@Param("id") String id);

    @Select("WITH author_following AS (\n" +
            "    -- People I am currently following\n" +
            "    SELECT author AS followed_user\n" +
            "    FROM author_followers\n" +
            "    WHERE follower = #{authorID}::uuid\n" +
            "),\n" +
            "friends_followed_authors AS (\n" +
            "    -- Authors followed by the people I follow\n" +
            "    SELECT DISTINCT af.author AS suggested_author\n" +
            "    FROM author_followers af\n" +
            "    WHERE af.follower IN (SELECT followed_user FROM author_following)\n" +
            "      AND af.author != #{authorID}::uuid\n" +
            "),\n" +
            "recommended_authors AS (\n" +
            "    -- Filter out authors I already follow\n" +
            "    SELECT suggested_author\n" +
            "    FROM friends_followed_authors\n" +
            "    WHERE suggested_author NOT IN (\n" +
            "        SELECT followed_user FROM author_following\n" +
            "    )\n" +
            ")\n" +
            "SELECT a.*\n" +
            "FROM authors a\n" +
            "JOIN recommended_authors ra ON a.id = ra.suggested_author\n" +
            "ORDER BY a.id\n" +
            "LIMIT #{limit}")
    List<AuthorDto> getRecommendedAuthors(
            @Param("authorID") String authorID,
            @Param("tagId") String tagId,
            @Param("limit") Long limit);

    @Select("<script>\n" +
            "SELECT COUNT(DISTINCT a.id)\n" +
            "FROM authors a\n" +
            "<where>\n" +
            "    (\n" +
            "        a.username ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.email ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.firstname ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.lastname ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.address ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.biography ILIKE CONCAT('%', #{term}, '%')\n" +
            "    )\n" +
            "    <choose>\n" +
            "        <when test=\"startDate != null and endDate != null\">\n" +
            "            AND a.createdAt BETWEEN #{startDate} AND #{endDate}\n" +
            "        </when>\n" +
            "        <when test=\"startDate != null\">\n" +
            "            AND a.createdAt &gt;= #{startDate}\n" +
            "        </when>\n" +
            "        <when test=\"endDate != null\">\n" +
            "            AND a.createdAt &lt;= #{endDate}\n" +
            "        </when>\n" +
            "    </choose>\n" +
            "</where>\n" +
            "</script>")
    Long totalSearchAuthors(
            @Param("term") String term,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Select("<script>\n" +
            "SELECT a.*\n" +
            "FROM authors a\n" +
            "<where>\n" +
            "    -- Search Term Filter\n" +
            "    (\n" +
            "        a.username ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.email ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.firstname ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.lastname ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.address ILIKE CONCAT('%', #{term}, '%')\n" +
            "        OR a.biography ILIKE CONCAT('%', #{term}, '%')\n" +
            "    )\n" +
            "    <choose>\n" +
            "        <when test=\"startDate != null and endDate != null\">\n" +
            "            AND a.createdAt BETWEEN #{startDate} AND #{endDate}\n" +
            "        </when>\n" +
            "        <when test=\"startDate != null\">\n" +
            "            AND a.createdAt &gt;= #{startDate}\n" +
            "        </when>\n" +
            "        <when test=\"endDate != null\">\n" +
            "            AND a.createdAt &lt;= #{endDate}\n" +
            "        </when>\n" +
            "    </choose>\n" +
            "</where>\n" +
            "GROUP BY a.id\n" +
            "ORDER BY a.createdAt DESC\n" +
            "LIMIT #{limit} OFFSET #{start}\n" +
            "</script>")
    List<AuthorDto> searchAuthors(
            @Param("term") String term,
            @Param("limit") Long limit,
            @Param("start") Long start,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Select("WITH my_following AS (\n" +
            "    SELECT author \n" +
            "    FROM author_followers \n" +
            "    WHERE follower = #{authorId}::uuid\n" +
            "),\n" +
            "friends_of_friends AS (\n" +
            "    SELECT \n" +
            "        af2.author AS suggested_author,\n" +
            "        COUNT(af2.follower) AS mutual_friend_count\n" +
            "    FROM author_followers af1\n" +
            "    JOIN author_followers af2 ON af1.author = af2.follower\n" +
            "    WHERE af1.follower = #{authorId}::uuid\n" +
            "      AND af2.author != #{authorId}::uuid\n" +
            "    GROUP BY af2.author\n" +
            ")\n" +
            "SELECT \n" +
            "    a.*, \n" +
            "    fof.mutual_friend_count\n" +
            "FROM authors a\n" +
            "JOIN friends_of_friends fof ON a.id = fof.suggested_author\n" +
            "JOIN author_interest ai ON ai.authorID = a.id\n" +
            "WHERE ai.tagID = #{tagId}::uuid\n" +
            "  AND NOT EXISTS (\n" +
            "      SELECT 1 \n" +
            "      FROM my_following mf \n" +
            "      WHERE mf.author = a.id\n" +
            "  )\n" +
            "ORDER BY fof.mutual_friend_count DESC, a.id\n" +
            "LIMIT #{limit};")
    List<AuthorDto> findRecommendedInterestAuthor(
            @Param("authorId") String authorId,
            @Param("tagId") String tagId,
            @Param("limit") Long limit);

    @Select("SELECT EXISTS (SELECT 1 FROM authors WHERE id = #{id}::uuid)")
    boolean isAuthorExist(@Param("id") String id);


}
