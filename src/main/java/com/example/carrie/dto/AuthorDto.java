package com.example.carrie.dto;

import com.example.carrie.models.Author;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthorDto {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate dob;
    private String gender;
    private List<String> interests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String msisdn;
    private String address;
    private String biography;
    private Long followers = 0L;
    private Long following = 0L;

    public AuthorDto AuthorDtoMapper(
            Author author
    ) {
        this.setId(author.getId());
        this.setFirstName(author.getFirstName());
        this.setLastName(author.getLastName());
        this.setEmail(author.getEmail());
        this.setDob(author.getDob());
        this.setGender(author.getGender());
        this.setInterests(author.getInterests());
        this.setMsisdn(author.getMsisdn());
        this.setAddress(author.getAddress());
        this.setBiography(author.getBiography());
        this.setFollowers(author.getFollowers());
        this.setFollowing(author.getFollowing());
        this.setUsername(author.getUsername());
        return this;
    }
}
