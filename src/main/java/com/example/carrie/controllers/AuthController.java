package com.example.carrie.controllers;

import com.example.carrie.exceptions.custom.BadRequest;
import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import com.example.carrie.services.impl.AuthServiceImpl;
import com.example.carrie.services.impl.AuthorServiceImpl;
import com.example.carrie.success.Success;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @Valid @RequestPart("author") Author author,
            @RequestPart(value = "image", required = false) MultipartFile image,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequest(result.getAllErrors().get(0).getDefaultMessage());
        }

        List<Author> data = Collections.singletonList(
                authorServiceImpl.addAuthor(author, image)
        );
        return Success.CREATED("Successfully created Author.", data);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login){
        String token = authServiceImpl.verify(login);
        return Success.OK("Login Successfully",token);
    }
}
