package com.example.carrie.services;

import com.example.carrie.entities.Comment;
import com.example.carrie.models.CustomData;

public interface CommentService {
  public Comment getCommentById(String id);

  public CustomData getArticleComments(String articleID, Long limit, Long start);

  public Comment addComment(Comment comment);

  public Comment editComment(Comment comment, String id);

  public Comment deleteComment(String id);

}
