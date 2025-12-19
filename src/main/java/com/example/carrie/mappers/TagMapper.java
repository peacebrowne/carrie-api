package com.example.carrie.mappers;

import org.apache.ibatis.annotations.*;

import com.example.carrie.models.Tag;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TagMapper {
  @Select("SELECT * FROM tags WHERE name = #{name}")
  Tag getByName(@Param("name") String name);

  @Select("SELECT * FROM tags WHERE id = #{id}::uuid")
  Tag getById(@Param("id") String id);

  @Select("SELECT * FROM tags")
  List<Tag> getAll();

  @Select("INSERT INTO tags (name) VALUES(#{name}) RETURNING *")
  Tag addTags(Tag tag);

  @Select("SELECT t.name FROM tags t LEFT JOIN article_tags at ON at.tagID = t.id WHERE at.articleID = #{articleID}::uuid")
  List<Tag> getArticleTags(@Param("articleID") String articleID);

  @Select("SELECT DISTINCT t.* FROM tags t LEFT JOIN author_interest at ON at.tagID = t.id  WHERE at.authorID = #{authorID}::uuid")
  List<Tag> getAuthorTags(@Param("authorID") String authorID);

    @Select("SELECT * FROM tags WHERE name ILIKE CONCAT('%', #{term}, '%') ")
    List<Tag> searchTags(@Param("term") String term);

    @Insert("INSERT INTO author_interest (authorID, tagID) VALUES(#{authorID}::uuid, #{tagID}::uuid)")
    void addAuthorInterest(@Param("authorID") String authorID, @Param("tagID") String tagID);

    @Select("SELECT * FROM author_interest WHERE authorId = #{authorId}::uuid AND tagId = #{tagId}::uuid")
    Optional<Tag> getSingleAuthorInterest(@Param("authorId") String authorId, @Param("tagId") String tagId);

    @Select("UPDATE tags SET name = #{name}, popularity = #{popularity}, stories = #{stories} WHERE id = #{id}::uuid RETURNING *")
    Tag updateTag(Tag tag);

    @Insert("INSERT INTO article_tags (articleID, tagID) VALUES(#{articleID}::uuid, #{tagID}::uuid)")
    void addArticleTag(@Param("articleID") String articleID, @Param("tagID") String tagID);

    @Delete("DELETE FROM article_tags WHERE articleID = #{articleID}::uuid")
    void deleteArticleTag(@Param("articleID") String articleID);

  @Delete("DELETE FROM author_interest WHERE authorID = #{authorID}::uuid")
  void deleteAuthorInterests(@Param("authorID") String authorID);

  @Delete("DELETE FROM author_interest WHERE authorId = #{authorId}::uuid AND tagId = #{tagId}::uuid")
  void deleteAuthorInterest(@Param("authorId") String authorId, @Param("tagId") String tagId);

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
          "recommended_tag AS (\n" +
          "    SELECT \n" +
          "        ai.tagid\n" +
          "    FROM author_interest ai\n" +
          "    WHERE ai.authorid IN (SELECT authorid FROM similar_authors)\n" +
          "      AND ai.tagid NOT IN (SELECT tagid FROM author_tags)\n" +
          "    GROUP BY ai.tagid\n" +
          ")\n" +
          "SELECT\n" +
          "    t.id,\n" +
          "    t.name,\n" +
          "    t.stories, \n" +
          "    t.popularity \n" +
          "FROM recommended_tag rt\n" +
          "JOIN tags t ON t.id = rt.tagid\n" +
          "ORDER BY \n" +
          "    t.stories DESC,\n" +
          "    t.popularity DESC,\n" +
          "    t.name\n" +
          "LIMIT #{limit};")
    List<Tag> getAuthorRecommendedInterest(@Param("authorID") String authorID, @Param("limit") Long limit);
  
    @Select("WITH subtopics AS (\n" +
            "  SELECT * FROM tags WHERE id = #{parentTagId}::uuid OR parentTagId = #{parentTagId}::uuid" +
            "),\n" +
            "childTopics AS (\n" +
            "  SELECT DISTINCT *\n" +
            "  FROM tags\n" +
            "  WHERE parentTagId IN (SELECT id FROM subtopics)\n" +
            "),\n" +
            "\n" +
            "randomized AS (\n" +
            "  SELECT *\n" +
            "  FROM (\n" +
            "    SELECT * FROM subtopics\n" +
            "    UNION\n" +
            "    SELECT * FROM childTopics\n" +
            "  ) AS combined\n" +
            "  ORDER BY random()\n" +
            "  LIMIT 9   \n" +
            ")\n" +
            "\n" +
            "SELECT * FROM randomized\n" +
            "UNION\n" +
            "SELECT * FROM tags WHERE id = #{tagId}::uuid;")
    List<Tag> getRandomTags(
            @Param("parentTagId") String parentTagId,
            @Param("tagId") String tagId, 
            @Param("limit") Long limit
    );
}


