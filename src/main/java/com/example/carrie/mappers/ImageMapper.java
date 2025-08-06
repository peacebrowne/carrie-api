package com.example.carrie.mappers;

import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.example.carrie.models.Image;

@Mapper
public interface ImageMapper {

  @Select("SELECT * FROM images WHERE id = {id}::uuid")
  Image findById(@Param("id") String id);

  @Select("SELECT * FROM images WHERE targetID = #{targetID}::uuid")
  Optional<Image> findImageByTarget(@Param("targetID") String targetID);

  @Select("INSERT INTO images (name, targetID, type, data) VALUES (#{name}, #{targetID}::uuid, #{type}, #{data}) RETURNING *")
  Image addImage(Image image);

  @Select("UPDATE images SET name = #{name}, targetID = #{targetID}::uuid, type = #{type}, data = #{data} WHERE id = #{id}::uuid RETURNING *")
  Image editImage(
          @Param("name") String name,
          @Param("targetID") String targetID,
          @Param("type") String type,
          @Param("data") byte[] data,
          @Param("id") String id);

  @Delete("DELETE FROM images WHERE id = #{id}::uuid")
  void deleteImage(@Param("id") String id);

}
