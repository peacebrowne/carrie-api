package com.example.carrie.controllers;

import com.example.carrie.entities.Author;
import com.example.carrie.services.impl.AuthorServiceImpl;
import com.example.carrie.success.Success;

import jakarta.validation.Valid;

import com.example.carrie.errors.custom.BadRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
        List<Author> data = Arrays.asList(author);

        return Success.OK("Successfully Retrieved Author", data);
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors(
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "10") Long limit,
            @RequestParam(required = false, defaultValue = "0") Long start) {

        List<Author> data = authorServiceImpl.getAllAuthors(sort, limit, start);
        return Success.OK("Successfully Retrieved all Authors.", data);
    }

    @PostMapping
    public ResponseEntity<?> addAuthor(@Valid @RequestBody(required = true) Author author, BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());
        }

        Author createdAuthor = authorServiceImpl.addAuthor(author);
        List<Author> data = Arrays.asList(createdAuthor);

        return Success.CREATED("Successfully created Author.", data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAuthor(@RequestBody Author author, @PathVariable(required = true) String id) {

        Author editedAuthor = authorServiceImpl.editAuthor(author, id);
        List<Author> data = Arrays.asList(editedAuthor);

        return Success.OK("Successfully Updated Author", data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable(required = true) String id) {
        Author deletedAuthor = authorServiceImpl.deleteAuthor(id);
        List<Author> data = Arrays.asList(deletedAuthor);

        return Success.OK("Successfully Deleted Author", data);
    }
}
