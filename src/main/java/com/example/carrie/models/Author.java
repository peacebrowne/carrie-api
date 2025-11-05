package com.example.carrie.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Author {

    // @Getter(AccessLevel.NONE)
    private String password;

    /*
     * ID of the Comment.
     * This field represents a unique identifier for the comment.
     */
    private String id;

    /*
     * First name of the author.
     * This field is required and should not exceed 20 characters.
     */
    @NotEmpty(message = "Please provide a first name")
    @Size(max = 20, message = "Username should not be more than 20 characters")
    private String firstName;

    /*
     * Last name of the author.
     * This field is required and should not exceed 20 characters.
     */
    private String lastName;

    /*
     * Username of the author.
     * This field is required and should not exceed 20 characters.
     */
    @NotEmpty(message = "Please provide a username")
    @Size(max = 20, message = "Username should not be more than 20 characters")
    private String username;

    /*
     * Email address of the user who posted the comment.
     * This field is required and must be a valid email address.
     */
    @NotEmpty(message = "Please provide an email address")
    @Email(message = "Invalid email address")
    private String email;

    /*
     * Date of birth of the user who posted the comment.
     * This field must be in the past, and it should follow the "yyyy-mm-dd" date
     * format.
     */
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @Past(message = "Date of birth must be in the past!")
    private LocalDate dob;

    /*
     * Gender of the user who posted the comment.
     * This is an optional field and can contain the gender value.
     */
    private String gender;

    /**
     * List of topics that the author has interest in reading
     * Tags/Categories ids.
     */
    private List<String> interests;

    /*
     * Local date and time the Image was created.
     * This field records the timestamp when the image was first created.
     */
    private LocalDateTime createdAt;

    /*
     * Local date and time the Image was updated.
     * This field stores the timestamp when the image was last updated.
     */
    private LocalDateTime updatedAt;

    private String msisdn;

    private String address;

    private String biography;

    private Long followers = 0L;

    private Long following = 0L;

}
