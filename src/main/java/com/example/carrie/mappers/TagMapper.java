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

  @Insert("INSERT INTO author_interest (author_id, tag_id) VALUES(#{authorID}::uuid, #{tagID}::uuid)")
  void addAuthorInterest(String authorID, String tagID);

  @Insert("INSERT INTO article_tags (articleID, tagID) VALUES(#{articleID}::uuid, #{tagID}::uuid)")
  void addArticleTag(String articleID, String tagID);

  @Delete("DELETE FROM article_tags WHERE articleID = #{articleID}::uuid")
  void deleteArticleTag(@Param("articleID") String articleID);


}
