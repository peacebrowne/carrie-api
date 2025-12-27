package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.example.carrie.models.Clap;

@Mapper
public interface ClapMapper {

  @Select("SELECT * FROM claps WHERE authorID = #{authorID}::uuid AND (articleID = #{targetID}::uuid OR commentID = #{targetID}::uuid)")
  Clap findClapByAuthorAndTarget(@Param("authorID") String authorID, @Param("targetID") String targetID);

  @Select("SELECT * FROM claps WHERE id = #{id}::uuid")
  Clap findById(@Param("id") String id);

  @Select("SELECT * FROM claps WHERE articleID = #{targetID}::uuid OR commentID = #{targetID}::uuid")
  List<Clap> findClapByTarget(@Param("targetID") String targetID);

  @Select("INSERT INTO claps (authorID, articleID, commentID) VALUES (#{authorID}::uuid, #{articleID}::uuid, #{commentID}::uuid) RETURNING *")
  Clap addClap(Clap clap);

  @Update("<script>" +
      "UPDATE claps SET" +
      "<choose>" +
          "<when test='target == \"like\"'>" +
            "likes = #{count}" +
          "</when>" +
          "<when test='target == \"dislike\"'>" +
            "dislikes = #{count}" +
          "</when>" +
      "</choose>" +
      "WHERE id = #{id}::uuid" +
      "</script>"
  )
  void updateClapCount(@Param("id") String id, @Param("count") Long count, @Param("target") String target);

  @Update("UPDATE claps SET dislikes = #{dislikes} WHERE id = #{id}::uuid")
  void updateClapDislike(@Param("id") String id, @Param("dislikes") String dislikes);

  @Delete("DELETE FROM claps WHERE id = #{id}::uuid")
  void deleteClap(@Param("id") String id);

  @Select("SELECT COALESCE(SUM(cl.likes), 0) FROM claps cl JOIN articles ar ON cl.articleid = ar.id WHERE ar.authorid = #{authorId}::uuid;")
  int countClaps(String authorId);

  @Select("SELECT COALESCE(cl.likes, 0) FROM claps cl WHERE cl.articleId = #{articleId}::uuid")
  int articleClaps(String articleId);

}
