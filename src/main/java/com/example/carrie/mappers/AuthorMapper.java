package com.example.carrie.mappers;

import com.example.carrie.entities.Author;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AuthorMapper {

    @Select("SELECT * FROM authors ORDER BY #{sort} DESC LIMIT #{limit} OFFSET #{start}")
    List<Author> findAll(@Param("sort") String sort, @Param("limit") Long limit, @Param("start") Long start);

    @Select("SELECT * FROM authors WHERE id = #{id}::uuid")
    Author findById(@Param("id") String id);

    @Select("SELECT * FROM authors WHERE email = #{email}")
    Optional<Author> findByEmail(@Param("email") String email);

    @Select("INSERT INTO authors (name, email, dob, gender) VALUES (#{name}, #{email}, #{dob}, #{gender}) RETURNING *")
    Author addAuthor(Author author);

    @Select("UPDATE authors SET name = #{name}, email = #{email}, dob = #{dob},  gender = #{gender} WHERE id = #{id}::uuid RETURNING *")
    Author editAuthor(Author author);

    @Select("DELETE FROM authors WHERE id = #{id}::uuid RETURNING *")
    Author deleteAuthor(@Param("id") String id);
}