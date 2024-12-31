package com.example.carrie.mappers;

import com.example.carrie.entities.Author;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuthorMapper {

    @Select("SELECT * FROM authors ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Author> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT * FROM authors WHERE id = #{id}::uuid")
    Author findById(@Param("id") String id);

    @Select("SELECT * FROM authors WHERE email = #{email}")
    Author findByEmail(@Param("email") String email);

    @Select("INSERT INTO authors (username, email, dob, gender, firstName, lastName) VALUES (#{username}, #{email}, #{dob}, #{gender}, #{firstName}, #{lastName}) RETURNING *")
    Author addAuthor(Author author);

    @Select("UPDATE authors SET username = #{username}, email = #{email}, dob = #{dob},  gender = #{gender}, firstName = #{firstName}, lastName = #{lastName} WHERE id = #{id}::uuid RETURNING *")
    Author editAuthor(Author author);

    @Select("DELETE FROM authors WHERE id = #{id}::uuid RETURNING *")
    Author deleteAuthor(@Param("id") String id);
}
