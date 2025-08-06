package com.example.carrie.mappers;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;
import com.example.carrie.models.Comment;

@Mapper
public interface CommentMapper {
  @Select("INSERT INTO comments (content, articleID, authorID, parentCommentID) VALUES(#{content}, #{articleID}::uuid, #{authorID}::uuid, #{parentCommentID}::uuid) RETURNING *")
  Comment addComment(Comment comment);

  @Select("SELECT * FROM comments WHERE id = #{id}::uuid")
  Optional<Comment> findById(@Param("id") String id);

  @Select("SELECT " +
      "c.*, " +
      "(SELECT " +
      "COUNT(cm.*) AS totalReplies " +
      "FROM " +
      "comments cm " +
      "WHERE " +
      "cm.parentCommentID = c.id), " +
      "SUM(cl.likes) AS totalLikes, " +
      "SUM(cl.dislikes) AS totalDislikes " +
      "FROM " +
      "comments c " +
      "LEFT JOIN " +
      "claps cl ON cl.commentID = c.id " +
      "WHERE " +
      "c.articleID = #{articleID}::UUID " +
      "GROUP BY c.id " +
      "ORDER BY createdAt DESC")
  List<Comment> findArticleComments(@Param("articleID") String articleID);

  @Select("SELECT COUNT(*) AS total FROM (SELECT * FROM comments WHERE articleID = #{id}::uuid OR parentCommentID = #{id}::uuid)")
  Long getTotalComments(@Param("id") String id);

  @Select("SELECT " +
      "c.*, " +
      "(SELECT " +
      "COUNT(cm.*) AS totalReplies " +
      "FROM " +
      "comments cm " +
      "WHERE " +
      "cm.parentCommentID = c.id), " +
      "SUM(cl.likes) AS totalLikes, " +
      "SUM(cl.dislikes) AS totalDislikes " +
      "FROM " +
      "comments c " +
      "LEFT JOIN " +
      "claps cl ON cl.commentID = c.id " +
      "WHERE " +
      "c.parentCommentID = #{id}::uuid " +
      "GROUP BY " +
      "c.id " +
      "ORDER BY " +
      "createdAt DESC")
  List<Comment> findCommentReplies(@Param("id") String id);

  @Update("UPDATE comments SET content = #{content} WHERE id = #{id}::uuid")
  void editComment(Comment comment);

  @Delete("DELETE FROM comments WHERE id = #{id}::uuid")
  void deleteComment(@Param("id") String id);

}
