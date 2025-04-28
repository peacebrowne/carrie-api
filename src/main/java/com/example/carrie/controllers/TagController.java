package com.example.carrie.controllers;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.services.impl.TagServiceImpl;
import com.example.carrie.success.Success;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/tags")
@RestController
@CrossOrigin
public class TagController {

    private final TagServiceImpl tagService;

    public TagController(TagServiceImpl tagService){
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTags(){
        return Success.OK("Successfully Retrieved all Tags", tagService.getAllTags());
    }

}
