package com.example.carrie.mappers;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.example.carrie.entities.Comment;

@Mapper
public interface CommentMapper {
  @Select("INSERT INTO comments (content, articleID, authorID, parentCommentID) VALUES(#{content}, #{articleID}::uuid, #{authorID}::uuid, #{parentCommentID}::uuid) RETURNING *")
  Comment addComment(Comment comment);

  @Select("SELECT * FROM comments WHERE id = #{id}::uuid")
  Comment findById(@Param("id") String id);

  @Select("SELECT * FROM comments WHERE articleID = #{articleID}::uuid ORDER BY createdAt LIMIT #{limit} OFFSET #{start}")
  List<Comment> findArticleComments(@Param("articleID") String articleID, @Param("limit") Long limit,
      @Param("start") Long start);

  @Select("SELECT COUNT(*) AS total FROM (SELECT * FROM comments WHERE articleID = #{id}::uuid OR parentCommentID = #{id}::uuid)")
  Long totalComments(@Param("id") String id);

  @Select("SELECT * FROM comments WHERE parentCommentID = #{id}::uuid ORDER BY createdAt  LIMIT #{limit} OFFSET #{start}")
  List<Comment> findCommentReplies(@Param("id") String id, @Param("limit") Long limit,
      @Param("start") Long start);

  @Update("UPDATE comments SET content = #{content} WHERE id = #{id}::uuid")
  void editComment(Comment comment);

  @Delete("DELETE FROM comments WHERE id = #{id}::uuid")
  void deleteComment(@Param("id") String id);

}
