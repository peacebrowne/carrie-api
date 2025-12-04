package com.example.carrie.controllers;

import com.example.carrie.dto.AuthorDto;
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
        return Success.OK("Successfully Retrieved Author", authorServiceImpl.getAuthorById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors(
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "10") Long limit,
            @RequestParam(required = false, defaultValue = "0") Long start) {

        return Success.OK("Successfully Retrieved all Authors.", authorServiceImpl.getAllAuthors(sort, limit, start));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getAuthorFollowers(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "10") Long limit,
            @RequestParam(required = false, defaultValue = "0") Long start) {
        return Success.OK("Successfully Retrieved author followers", authorServiceImpl.getAuthorFollowers(id, limit, start));
    }

    @GetMapping("/{id}/followed")
    public ResponseEntity<?> getFollowedAuthors(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "10") Long limit,
            @RequestParam(required = false, defaultValue = "0") Long start) {
        return Success.OK("Successfully Retrieved author following", authorServiceImpl.getFollowedAuthors(id, limit, start));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> addAuthorFollower(
            @RequestParam String follower,
            @RequestParam String author) {
        return Success.CREATED("Successfully Added Author follower", authorServiceImpl.followAuthor(follower, author));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> removeAuthorFollower(
            @RequestParam String follower,
            @RequestParam String author) {
        return Success.CREATED("Successfully Added Author follower", authorServiceImpl.unfollowAuthor(follower, author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAuthor(
            @RequestBody Author author, @PathVariable String id) {
        return Success.OK("Successfully Updated Author", authorServiceImpl.editAuthor(author, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable(required = true) String id) {
        return Success.OK("Successfully Deleted Author", authorServiceImpl.deleteAuthor(id));
    }

    @GetMapping("/recommended/{authorID}")
    public ResponseEntity<?> getRecommendedTags(
            @PathVariable String authorID,
            @RequestParam(required = false, defaultValue = "10") Long limit
    ){
        return Success.OK("Successfully Retrieved Recommended Topics",
                authorServiceImpl.recommendedAuthors(authorID, limit));
    }

}
