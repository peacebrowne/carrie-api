package com.example.carrie.mappers;

import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.carrie.entities.Image;

@Mapper
public interface ImageMapper {

  @Select("SELECT * FROM images WHERE id = {id}::uuid")
  Image findById(@Param("id") String id);

  @Select("SELECT * FROM images WHERE targetID = #{targetID}::uuid")
  Optional<Image> findImageByTarget(@Param("targetID") String targetID);

  @Select("INSERT INTO images (name, targetID, type, data) VALUES (#{name}, #{targetID}::uuid, #{type}, #{data}) RETURNING *")
  Image addImage(Image image);

  @Update("UPDATE images SET name = #{name}, targetID = #{targetID}::uuid, type = #{type}, data = #{data}, updatedAt = #{updatedAt} WHERE id = #{id}::uuid")
  void editImage(Image image);

  @Delete("DELETE FROM images WHERE id = #{id}::uuid")
  void deleteImage(@Param("id") String id);

}
