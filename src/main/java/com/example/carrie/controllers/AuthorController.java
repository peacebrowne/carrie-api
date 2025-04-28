package com.example.carrie.controllers;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Author;
import com.example.carrie.services.impl.AuthorServiceImpl;
import com.example.carrie.success.Success;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorServiceImpl authorServiceImpl;

    public AuthorController(AuthorServiceImpl authorServiceImpl) {
        this.authorServiceImpl = authorServiceImpl;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findAuthorById(@PathVariable(required = true) String id) {
        Author author = authorServiceImpl.getAuthorById(id);
        return Success.OK("Successfully Retrieved Author", author);
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors(
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "10") Long limit,
            @RequestParam(required = false, defaultValue = "0") Long start) {

        List<Author> data = authorServiceImpl.getAllAuthors(sort, limit, start);
        return Success.OK("Successfully Retrieved all Authors.", data);
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getAuthorFollowers(
            @PathVariable String id
    ){
        CustomDto followers = authorServiceImpl.getAuthorFollowers(id);
        return Success.OK("Successfully Retrieved author followers", followers);
    }

    @GetMapping("/{id}/followed")
    public ResponseEntity<?> getFollowedAuthors(
            @PathVariable String id
    ){
        List<Author> followers = authorServiceImpl.getFollowedAuthors(id);
        return Success.OK("Successfully Retrieved author followers", followers);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> addAuthorFollower(
            @RequestParam String follower,
            @RequestParam String author
    ){
        Map<String, Object> data =  authorServiceImpl.followAuthor(follower, author);
        return Success.CREATED("Successfully Added Author follower", data);
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> removeAuthorFollower(
            @RequestParam String follower,
            @RequestParam String author
    ){
        Map<String, Object> data =  authorServiceImpl.unfollowAuthor(follower, author);
        return Success.CREATED("Successfully Added Author follower", data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAuthor(
            @RequestBody Author author, @PathVariable String id) {

        Author editedAuthor = authorServiceImpl.editAuthor(author, id);
        List<Author> data = Collections.singletonList(editedAuthor);

        return Success.OK("Successfully Updated Author", data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable(required = true) String id) {
        Author deletedAuthor = authorServiceImpl.deleteAuthor(id);
        List<Author> data = Collections.singletonList(deletedAuthor);

        return Success.OK("Successfully Deleted Author", data);
    }

}
