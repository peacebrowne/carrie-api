package com.example.carrie.mappers;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.example.carrie.entities.Clap;

@Mapper
public interface ClapMapper {

  @Select("SELECT * FROM claps WHERE authorID = #{authorID}::uuid AND (articleID = #{targetID}::uuid OR commentID = #{targetID}::uuid)")
  Optional<Clap> findClapByAuthorAndTarget(@Param("authorID") String authorID, @Param("targetID") String targetID);

  @Select("SELECT * FROM claps WHERE id = #{id}::uuid")
  Optional<Clap> findById(@Param("id") String id);

  @Select("SELECT * FROM claps WHERE articleID = #{targetID}::uuid OR commentID = #{targetID}::uuid")
  List<Clap> findClapByTarget(@Param("targetID") String targetID);

  @Select("INSERT INTO claps (authorID, articleID, commentID) VALUES (#{authorID}::uuid, #{articleID}::uuid, #{commentID}::uuid) RETURNING *")
  Clap addClap(Clap clap);

  @Update("UPDATE claps SET count = #{count} WHERE id = #{id}::uuid")
  void updateClapCount(@Param("id") String id, @Param("count") Long count);

  @Delete("DELETE FROM claps WHERE id = #{id}::uuid")
  void deleteClap(@Param("id") String id);

}
