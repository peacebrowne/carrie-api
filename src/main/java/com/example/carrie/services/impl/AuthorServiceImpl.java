package com.example.carrie.services.impl;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Author;
import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.Conflict;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;

import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.services.AuthorService;
import com.example.carrie.utils.validations.EmailValidator;
import com.example.carrie.utils.validations.UUIDValidator;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {
    private final AuthorMapper authorMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public AuthorServiceImpl(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    @Override
    public Author getAuthorById(String id) {
        try {

            validateAuthor(id);
            return authorMapper.findById(id);

        } catch (BadRequest | NotFound e) {
            log.error("ERROR: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the author.");
        }
    }

    @Override
    public List<Author> getAllAuthors(String sort, Long limit, Long start) {

        try {
            return authorMapper.findAll(sort, limit, start);
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching authors.");
        }

    }

    @Override
    public Author addAuthor(Author author) {

        try {
            validateEmail(author.getEmail());

            Optional<Author> authorExist = authorMapper.findByEmailOrUsername(author.getEmail());

            if (authorExist.isPresent())
                throw new BadRequest("Author already exist.");

            Optional<Author> usernameExist = authorMapper.findByEmailOrUsername(author.getUsername());
            if(usernameExist.isPresent())
                throw new Conflict("Username already exist.");

            author.setPassword(encoder.encode(author.getPassword()));

            return authorMapper.addAuthor(author);
        } catch (BadRequest | Conflict e) {
            log.error("Bad Request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while creating an account. Please try again!");

        }
    }

    @Override
    public Author editAuthor(Author author, String id) {
        try {

            Author existingAuthor = getAuthorById(id);

            if (author.getEmail() != null) {

                validateEmail(author.getEmail());

                if (!Objects.equals(author.getEmail(), existingAuthor.getEmail())) {

                    Optional<Author> authorOptional = authorMapper.findByEmailOrUsername(author.getEmail());

                    if (authorOptional.isPresent())
                        throw new Conflict("Email is already taken!");

                    existingAuthor.setEmail(author.getEmail());

                }
            }

            Optional.ofNullable(author.getDob()).ifPresent(existingAuthor::setDob);
            Optional.ofNullable(author.getGender()).ifPresent(existingAuthor::setGender);
            Optional.ofNullable(author.getUsername()).ifPresent(existingAuthor::setUsername);
            Optional.ofNullable(author.getFirstName()).ifPresent(existingAuthor::setFirstName);
            Optional.ofNullable(author.getLastName()).ifPresent(existingAuthor::setLastName);

            return authorMapper.editAuthor(existingAuthor);
        } catch (BadRequest | NotFound e) {
            log.error("Bad Request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while editing the author.");
        }
    }

    @Override
    public Author deleteAuthor(String id) {
        try {

            getAuthorById(id);

            return authorMapper.deleteAuthor(id);

        } catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError("An unexpected error occurred while deleting the author.");
        }

    }

    private void validateEmail(String email) {
        if (!EmailValidator.isValidEmail(email))
            throw new BadRequest("Invalid email address!");
    }

    @Override
    public Map<String, Object> followAuthor(String followerAuthor, String followedAuthor) {
        try{

            Arrays.asList(followerAuthor, followedAuthor).forEach(this::getAuthorById);
            Map<String, Object> data = authorMapper.getSingleAuthorFollower(
                    followerAuthor, followedAuthor);

            if (data != null)
                throw new Conflict("Author with this id: '"
                        + followerAuthor + "' is already following the author with this id: '"
                        + followedAuthor + "'");

            return authorMapper.addAuthorFollower(followerAuthor, followedAuthor);
            
        } catch (BadRequest | Conflict | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError("An unexpected error occurred while add the author follower.");
        }
    }

    @Override
    public CustomDto getAuthorFollowers(String id) {
        try {
            validateAuthor(id);

            List<Map<String, Object>> authorFollowers = authorMapper.getAuthorFollowers(id);
            return new CustomDto((long) authorFollowers.size(), authorFollowers);

        }
        catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the author followers.");
        }
    }


    private void validateAuthorFollower (String followerAuthor, String followedAuthor){
        Arrays.asList(followerAuthor, followedAuthor).forEach(this::getAuthorById);

    }

    private void validateUUID(String id) {
        if (!UUIDValidator.isValidUUID(id)) {
            throw new BadRequest("Invalid Author ID");
        }
    }


    private void validateAuthor(String authorID) {

        // Validate Author ID
        validateUUID(authorID);

        Author author = authorMapper.findById(authorID);
        if (author == null) {
            throw new NotFound("Author does not exist!");
        }
    }
}
