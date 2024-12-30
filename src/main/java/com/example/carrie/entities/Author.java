package com.example.carrie.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Author {

    /*
     * ID of the Comment.
     * This field represents a unique identifier for the comment.
     */
    private String id;

    /*
     * Username of the user who posted the comment.
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

    /*
     * Timestamp when the comment was created.
     * This field stores the date and time when the comment was created.
     */
    private LocalDateTime created_at;

}
