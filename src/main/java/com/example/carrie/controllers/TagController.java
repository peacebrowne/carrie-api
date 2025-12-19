package com.example.carrie.controllers;

import com.example.carrie.services.impl.TagServiceImpl;
import com.example.carrie.success.Success;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/tags")
@RestController
@CrossOrigin
public class TagController {

    private final TagServiceImpl tagServiceImpl;

    public TagController(TagServiceImpl tagServiceImpl) {
        this.tagServiceImpl = tagServiceImpl;
    }

    @GetMapping
    public ResponseEntity<?> getAllTags() {
        return Success.OK("Successfully Retrieved all Tags", tagServiceImpl.getAllTags());
    }

    @GetMapping("/recommended/{authorID}")
    public ResponseEntity<?> getRecommendedTags(
            @PathVariable String authorID,
            @RequestParam(required = false, defaultValue = "10") Long limit
    ){
        return Success.OK("Successfully Retrieved Recommended Topics",
                tagServiceImpl.recommendedAuthorInterests(authorID, limit));
    }

    @GetMapping("/recommended-random/{parentTagId}/{tagId}")
    public ResponseEntity<?> getRecommendedRandomTags(
            @PathVariable String parentTagId,
            @PathVariable String tagId,
            @RequestParam(required = false, defaultValue = "9") Long limit
    ){
        return Success.OK("Successfully Retrieved Recommended Topics",
                tagServiceImpl.randomRecommendedTags(parentTagId, tagId, limit));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getTagById(@PathVariable String id){
        return Success.OK("Successfully Retrieved Tag", tagServiceImpl.getTagById(id));
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchTags(@RequestParam String term){
        return Success.OK("Successfully Retrieved searched tags", tagServiceImpl.searchTags(term));
    }

    @PutMapping("/follow")
    public  ResponseEntity<?> addTagFollower(
            @RequestParam String tagId,
            @RequestParam String authorId
    ){
        return Success.OK("Successfully updated tag follower", tagServiceImpl.followTag(tagId, authorId));
    }


    @PutMapping("/unfollow")
    public  ResponseEntity<?> removeTagFollower(
            @RequestParam String tagId,
            @RequestParam String authorId
    ){
        return Success.OK("Successfully updated tag follower", tagServiceImpl.unfollowTag(tagId, authorId));
    }

    @GetMapping("/author-interest/{tagId}/{authorId}")
    public ResponseEntity<?> getSingleAuthorInterest(@PathVariable String tagId, @PathVariable String authorId){
        return Success.OK("Successfully Retrieved Tag", tagServiceImpl.getSingleAuthorInterest(tagId, authorId));
    }

    @GetMapping("/author-interests/{authorId}")
    public ResponseEntity<?> getAuthorInterests(@PathVariable String authorId){
        return Success.OK("Successfully Retrieved Tag", tagServiceImpl.getAuthorInterest(authorId));
    }

}
