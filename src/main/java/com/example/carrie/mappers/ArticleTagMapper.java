package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.carrie.entities.ArticleTag;
import com.example.carrie.entities.Tag;

@Mapper
public interface ArticleTagMapper {
  @Select("SELECT t.name FROM tags t LEFT JOIN article_tags at ON at.tagID = t.id  WHERE at.articleID = #{articleID}::uuid")
  List<Tag> getArticleTags(@Param("articleID") String articleID);

  @Insert("INSERT INTO article_tags (articleID, tagID) VALUES(#{articleID}::uuid, #{tagID}::uuid)")
  void addArticleTag(ArticleTag articleTag);

  @Delete("DELETE FROM article_tags WHERE articleID = #{articleID}::uuid")
  void deleteArticleTag(@Param("articleID") String articleID);

}
