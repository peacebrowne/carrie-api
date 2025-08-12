package com.example.carrie.mappers;

import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Tag;

import java.util.List;

@Mapper
public interface TagMapper {
  @Select("SELECT * FROM tags WHERE name = #{name}")
  Tag getByName(@Param("name") String name);

  @Select("SELECT * FROM tags")
  List<Tag> getAll();

  @Select("INSERT INTO tags (name) VALUES(#{name}) RETURNING *")
  Tag addTags(Tag tag);

  @Select("SELECT t.name FROM tags t LEFT JOIN article_tags at ON at.tagID = t.id  WHERE at.articleID = #{articleID}::uuid")
  List<Tag> getArticleTags(@Param("articleID") String articleID);

  @Select("SELECT t.name FROM tags t LEFT JOIN author_interest at ON at.tagID = t.id  WHERE at.authorID = #{authorID}::uuid")
  List<Tag> getAuthorTags(@Param("authorID") String authorID);

  @Insert("INSERT INTO author_interest (authorID, tagID) VALUES(#{authorID}::uuid, #{tagID}::uuid)")
  void addAuthorInterest(@Param("authorID") String authorID, @Param("tagID") String tagID);

  @Insert("INSERT INTO article_tags (articleID, tagID) VALUES(#{articleID}::uuid, #{tagID}::uuid)")
  void addArticleTag(@Param("authorID") String authorID, @Param("tagID") String tagID);

  @Delete("DELETE FROM article_tags WHERE articleID = #{articleID}::uuid")
  void deleteArticleTag(@Param("articleID") String articleID);

  @Delete("DELETE FROM author_interest WHERE authorID = #{authorID}::uuid")
  void deleteAuthorInterest(@Param("authorID") String authorID);

}
