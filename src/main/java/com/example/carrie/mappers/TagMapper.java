package com.example.carrie.mappers;

import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Tag;

import java.util.List;

@Mapper
public interface TagMapper {
  @Select("SELECT * FROM tags WHERE name = #{name}")
  Tag getByName(@Param("name") String name);

//  TODO - get tag with total popularity and stories
  @Select("SELECT * FROM tags WHERE id = #{id}")
  Tag getById(@Param("id") String id);

  @Select("SELECT * FROM tags")
  List<Tag> getAll();

  @Select("INSERT INTO tags (name) VALUES(#{name}) RETURNING *")
  Tag addTags(Tag tag);

  @Update("UPDATE tags SET stories = stories + 1 WHERE id = #{id}::uuid")
  void updateTagStories(@Param("id") String id);

  @Select("SELECT t.name FROM tags t LEFT JOIN article_tags at ON at.tagID = t.id  WHERE at.articleID = #{articleID}::uuid")
  List<Tag> getArticleTags(@Param("articleID") String articleID);

  @Select("SELECT t.name FROM tags t LEFT JOIN author_interest at ON at.tagID = t.id  WHERE at.authorID = #{authorID}::uuid")
  List<Tag> getAuthorTags(@Param("authorID") String authorID);

  @Select("SELECT * FROM tags WHERE name ILIKE CONCAT('%', #{term}, '%') ")
  List<Tag> searchTags(@Param("term") String term);

  @Insert("INSERT INTO author_interest (authorID, tagID) VALUES(#{authorID}::uuid, #{tagID}::uuid)")
  void addAuthorInterest(@Param("authorID") String authorID, @Param("tagID") String tagID);

  @Insert("INSERT INTO article_tags (articleID, tagID) VALUES(#{articleID}::uuid, #{tagID}::uuid)")
  void addArticleTag(@Param("articleID") String articleID, @Param("tagID") String tagID);

  @Delete("DELETE FROM article_tags WHERE articleID = #{articleID}::uuid")
  void deleteArticleTag(@Param("articleID") String articleID);

  @Delete("DELETE FROM author_interest WHERE authorID = #{authorID}::uuid")
  void deleteAuthorInterest(@Param("authorID") String authorID);

  @Select("WITH author_tags AS (\n" +
          "    SELECT tagid\n" +
          "    FROM author_interest\n" +
          "    WHERE authorid = #{authorID}::uuid\n" +
          "),\n" +
          "similar_authors AS (\n" +
          "    SELECT DISTINCT ai.authorid\n" +
          "    FROM author_interest ai\n" +
          "    WHERE ai.tagid IN (SELECT tagid FROM author_tags)\n" +
          "      AND ai.authorid <> #{authorID}::uuid\n" +
          "),\n" +
          "recommended_tags AS (\n" +
          "    SELECT ai.tagid\n" +
          "    FROM author_interest ai\n" +
          "    WHERE ai.authorid IN (SELECT authorid FROM similar_authors)\n" +
          "      AND ai.tagid NOT IN (SELECT tagid FROM author_tags)\n" +
          ")\n" +
          "SELECT\n" +
          "    t.id,\n" +
          "    t.name,\n" +
          "    COUNT(*) AS popularity\n" +
          "FROM recommended_tags r\n" +
          "JOIN tags t ON t.id = r.tagid\n" +
          "GROUP BY t.id, t.name\n" +
          "ORDER BY popularity DESC, t.name\n" +
          "  LIMIT 8;")
    List<Tag> getRecommendedTags(@Param("authorID") String authorID);

}
