package com.example.carrie.services;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.entities.Comment;

public interface CommentService {
  public Comment getCommentById(String id);

  public CustomDto getArticleComments(String articleID);

  public Comment addComment(Comment comment);

  public Comment editComment(Comment comment, String id);

  public Comment deleteComment(String id);

  public CustomDto getCommentReplies(String parentCommentID);

}
