package com.example.carrie.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.entities.Comment;
import com.example.carrie.success.Success;
import com.example.carrie.errors.custom.BadRequest;

import jakarta.validation.Valid;

import com.example.carrie.services.impl.CommentServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentServiceImpl commentServiceImpl;

  public CommentController(CommentServiceImpl commentServiceImpl) {
    this.commentServiceImpl = commentServiceImpl;
  }

  @GetMapping("/articles/{id}")
  public ResponseEntity<?> getArticleComments(@PathVariable String id,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {
    CustomDto data = commentServiceImpl.getArticleComments(id, limit, start);

    return Success.OK("Successfully Retrieved Article's comments", data);
  }

  @GetMapping("/replies/{id}")
  public ResponseEntity<?> getCommentReplies(@PathVariable String id,
      @RequestParam(required = false, defaultValue = "10") Long limit,
      @RequestParam(required = false, defaultValue = "0") Long start) {
    CustomDto data = commentServiceImpl.getCommentReplies(id, limit, start);

    return Success.OK("Successfully Retrieved Comment's replies", data);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCommentById(@PathVariable String id) {
    Comment data = commentServiceImpl.getCommentById(id);
    return Success.OK("Successfully Retrieved Single Comment", data);
  }

  @PostMapping
  public ResponseEntity<?> addComment(@Valid @RequestBody Comment comment, BindingResult result) {

    if (result.hasErrors())
      throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());

    Comment data = commentServiceImpl.addComment(comment);
    return Success.CREATED("Successfully Created Comment.", data);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editComment(@RequestBody Comment comment, @PathVariable String id) {
    Comment data = commentServiceImpl.editComment(comment, id);
    return Success.OK("Successfully Updated Comment.", data);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable String id) {
    Comment data = commentServiceImpl.deleteComment(id);
    return Success.OK("Successfully Deleted Comment.", data);
  }

}
