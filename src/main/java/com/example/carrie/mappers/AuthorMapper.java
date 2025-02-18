package com.example.carrie.mappers;

import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AuthorMapper {

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt FROM authors ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Author> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt FROM authors WHERE id = #{id}::uuid")
    Optional<Author> findById(@Param("id") String id);

    @Select("SELECT id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt FROM authors WHERE email = #{target} OR username = #{target}")
    Optional<Author> findByEmailOrUsername(@Param("target") String target);

    @Select("SELECT username, password FROM authors WHERE email = #{target} OR username = #{target}")
    Login findLoginDetails(@Param("target") String target);

    @Select("INSERT INTO authors (username, email, dob, gender, firstName, lastName, password) VALUES (#{username}, #{email}, #{dob}, #{gender}, #{firstName}, #{lastName}, #{password}) RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt")
    Author addAuthor(Author author);

    @Select("UPDATE authors SET username = #{username}, email = #{email}, dob = #{dob},  gender = #{gender}, firstName = #{firstName}, lastName = #{lastName} WHERE id = #{id}::uuid RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt")
    Author editAuthor(Author author);

    @Select("DELETE FROM authors WHERE id = #{id}::uuid RETURNING id, username, email, dob, gender, createdAt, firstName, lastName, updatedAt")
    Author deleteAuthor(@Param("id") String id);
}
