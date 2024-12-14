package com.example.carrie.entities;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

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
    private Date created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setGender(Date created_at) {
        this.created_at = created_at;
    }

}
