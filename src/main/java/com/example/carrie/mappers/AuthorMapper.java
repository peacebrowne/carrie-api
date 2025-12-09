package com.example.carrie.mappers;

import com.example.carrie.dto.AuthorDto;
import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import com.example.carrie.models.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
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

    @Select("<script> " +
            "WITH author_following_friends AS ( " +
            "    SELECT follower AS friend " +
            "    FROM author_followers " +
            "    WHERE author = #{authorID}::uuid " +
            "), " +
            "friends_followed_authors AS ( " +
            "    SELECT DISTINCT af.author AS suggested_author " +
            "    FROM author_followers af " +
            "    WHERE af.follower IN (SELECT friend FROM author_following_friends) " +
            "      AND af.author != #{authorID}::uuid " +
            "), " +
            "recommended_authors AS ( " +
            "    SELECT suggested_author " +
            "    FROM friends_followed_authors " +
            "    WHERE suggested_author NOT IN ( " +
            "        SELECT friend FROM author_following_friends " +
            "    ) " +
            ") " +
            "SELECT a.* " +
            "FROM recommended_authors ra " +
            "JOIN authors a ON a.id = ra.suggested_author " +
            " <if test=\"tagId != null and tagId != ''\"> " +
            "     JOIN author_interest ai ON ai.authorId = a.id " +
            "       WHERE ai.tagId = #{tagId}::uuid " +
            " </if> " +
            "ORDER BY RANDOM() " +
            "LIMIT #{limit} " +
            "</script>")
    List<AuthorDto> getRecommendedAuthors(
            @Param("authorID") String authorID,
            @Param("tagId") String tagId,
            @Param("limit") Long limit);


}
