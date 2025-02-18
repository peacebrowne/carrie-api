package com.example.carrie.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.carrie.models.Tag;

@Mapper
public interface TagMapper {
  @Select("SELECT * FROM tags WHERE name = #{name}")
  Tag getByName(@Param("name") String name);

  @Select("INSERT INTO tags (name) VALUES(#{name}) RETURNING *")
  Tag addTags(Tag tag);

}
