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

    private String id;

    @NotEmpty(message = "Please provide a username")
    @Size(max = 20, message = "Username should not be more than 20 characters")
    private String name;

    @NotEmpty(message = "Please provide an email address")
    @Email(message = "Invalid email address")
    private String email;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @Past(message = "Date of birth must be in the past!")
    private LocalDate dob;

    private String gender;
    private LocalDateTime created_at;

}
