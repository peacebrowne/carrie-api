package com.example.carrie.controllers;

import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import com.example.carrie.services.impl.AuthServiceImpl;
import com.example.carrie.services.impl.AuthorServiceImpl;
import com.example.carrie.success.Success;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {

    @Autowired
    AuthorServiceImpl authorServiceImpl;

    @Autowired
    AuthServiceImpl authServiceImpl;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Author author, BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());
        }

        Author createdAuthor = authorServiceImpl.addAuthor(author);
        List<Author> data = Collections.singletonList(createdAuthor);

        return Success.CREATED("Successfully created Author.", data);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login){
        String token = authServiceImpl.verify(login);
        return Success.OK("Login Successfully",token);
    }
}
