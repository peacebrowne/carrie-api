package com.example.carrie.mappers;

import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface AuthorMapper {

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt FROM authors ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Author> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt FROM authors WHERE id = #{id}::uuid")
    Author findById(@Param("id") String id);

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt FROM authors WHERE email = #{target} OR username = #{target}")
    Optional<Author> findByEmailOrUsername(@Param("target") String target);

    @Select("SELECT username, password FROM authors WHERE email = #{target} OR username = #{target}")
    Login findLoginDetails(@Param("target") String target);

    @Select("INSERT INTO authors (username, email, dob, gender, firstName, lastName, password, address, msisdn, biography) VALUES (#{username}, #{email}, #{dob}, #{gender}, #{firstName}, #{lastName}, #{password}, #{address}, #{msisdn}, #{biography}) RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt")
    Author addAuthor(Author author);

    @Select("UPDATE authors SET username = #{username}, email = #{email}, dob = #{dob},  gender = #{gender}, firstName = #{firstName}, lastName = #{lastName}, address = #{address}, msisdn = #{msisdn}, biography = #{biography} WHERE id = #{id}::uuid RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt")
    Author editAuthor(Author author);

    @Select("DELETE FROM authors WHERE id = #{id}::uuid RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, address, msisdn, biography, updatedAt")
    Author deleteAuthor(@Param("id") String id);

    @Select("INSERT INTO author_followers (follower, author) VALUES (#{follower}::uuid ,#{author}::uuid) RETURNING *")
    Map<String, Object> followAuthor(@Param("follower") String follower, @Param("author") String author);

    @Select("DELETE FROM author_followers WHERE follower = #{follower}::uuid AND author = #{author}::uuid RETURNING *")
    Map<String, Object> unfollowAuthor(@Param("follower") String follower, @Param("author") String author);

    @Select("SELECT * FROM author_followers WHERE follower = #{follower}::uuid AND author = #{author}::uuid")
    Map<String, Object> getSingleAuthorFollower(@Param("follower") String follower, @Param("author") String author);

    @Select("SELECT a.id, a.firstName, a.lastName, a.username, a.dob, a.gender, a.msisdn, a.email, a.address, a.biography FROM authors a LEFT JOIN author_followers af ON a.id = af.follower WHERE af.author = #{id}::uuid")
    List<Map<String, Object>> getAuthorFollowers(@Param("id") String id);

    @Select("SELECT a.id, a.username, a.email, a.dob, a.gender, a.createdAt, a.firstName, a.lastName, a.address, a.msisdn, a.biography, a.updatedAt FROM author_followers af LEFT JOIN authors a ON a.id = af.author WHERE af.follower = #{id}::uuid")
    List<Author> getFollowedAuthors(@Param("id") String id);
}
