package com.example.carrie.services.impl;

import com.example.carrie.entities.Author;
import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;

import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.services.AuthorService;
import com.example.carrie.utils.EmailValidator;
import com.example.carrie.utils.UUIDValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorMapper authorMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    @Autowired
    public AuthorServiceImpl(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    @Override
    public Author getAuthorById(String id) {
        try {

            if (!UUIDValidator.isValidUUID(id)) {
                throw new BadRequest("Invalid author ID!");
            }

            Author author = authorMapper.findById(id);

            if (author == null) {
                throw new NotFound("Author with this id '" + id + "' does not exist!");
            }

            return authorMapper.findById(id);
        } catch (BadRequest | NotFound e) {
            log.error("ERROR:  " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ERROR:  " + e.getMessage());
            throw new InternalServerError(
                    "An unexpected error occured while fetching the author.");
        }
    }

    @Override
    public List<Author> getAllAuthors(String sort, Long limit, Long start) {

        try {
            return authorMapper.findAll(sort, limit, start);
        } catch (Exception e) {
            log.error("ERROR:  " + e.getMessage());
            throw new InternalServerError(
                    "An unexpected error occured while fetching authors.");
        }

    }

    @Override
    public Author addAuthor(Author author) {

        try {
            String email = author.getEmail();

            if (!EmailValidator.isValidEmail(email)) {
                throw new BadRequest("Invalid email address!");
            }

            Optional<Author> authorExist = authorMapper.findByEmail(email);

            if (authorExist.isPresent()) {
                throw new BadRequest("User with this email '" + email + "' already exist");
            }

            return authorMapper.addAuthor(author);
        } catch (BadRequest e) {
            log.error("ERROR:  " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ERROR:  " + e.getMessage());
            throw new InternalServerError(
                    "An unexpected error occured while adding the author.");

        }
    }

    @Override
    public Author editAuthor(Author a, String id) {
        try {

            if (!UUIDValidator.isValidUUID(id)) {
                throw new BadRequest("Invalid author ID!");
            }

            Author author = authorMapper.findById(id);

            if (author == null) {
                throw new NotFound("Author with this id '" + id + "' does not exist!");
            }

            if (a.getEmail() != null) {

                if (!EmailValidator.isValidEmail(a.getEmail())) {
                    throw new BadRequest("Invalid email address!");
                }

                if (!Objects.equals(a.getEmail(), author.getEmail())) {

                    Optional<Author> authorOptional = authorMapper.findByEmail(a.getEmail());

                    if (authorOptional.isPresent()) {
                        throw new BadRequest("Email is already taken!");
                    }

                    author.setEmail(a.getEmail());

                }
            }

            Optional.ofNullable(a.getDob()).ifPresent(dob -> author.setDob(dob));
            Optional.ofNullable(a.getGender()).ifPresent(gender -> author.setGender(gender));
            Optional.ofNullable(a.getName()).ifPresent(name -> author.setName(name));

            return authorMapper.editAuthor(author);
        } catch (BadRequest | NotFound e) {
            log.error("ERROR:  " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ERROR:  " + e.getMessage());
            throw new InternalServerError(
                    "An unexpected error occured while editing the author.");
        }
    }

    @Override
    public Author deleteAuthor(String id) {

        try {
            if (!UUIDValidator.isValidUUID(id)) {
                throw new BadRequest("Invalid author ID");
            }

            Author author = getAuthorById(id);

            if (author == null) {
                throw new NotFound("Author with this id '" + id + "' does not exist!");
            }

            return authorMapper.deleteAuthor(id);
        } catch (BadRequest | NotFound e) {
            log.error("ERROR:  " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ERROR:  " + e.getMessage());
            throw new InternalServerError(
                    "An unexpected error occured while deleting the author.");
        }

    }

}
