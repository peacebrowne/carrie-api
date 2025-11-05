package com.example.carrie.services.impl;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.mappers.ImageMapper;
import com.example.carrie.mappers.TagMapper;
import com.example.carrie.models.Author;
import com.example.carrie.exceptions.custom.BadRequest;
import com.example.carrie.exceptions.custom.Conflict;
import com.example.carrie.exceptions.custom.InternalServerError;
import com.example.carrie.exceptions.custom.NotFound;

import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.models.Tag;
import com.example.carrie.services.AuthorService;
import com.example.carrie.utils.validations.EmailValidator;
import com.example.carrie.utils.validations.UUIDValidator;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class AuthorServiceImpl extends ImageServiceImpl implements AuthorService {
    private final AuthorMapper authorMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final TagServiceImpl tagServiceImpl;

    public AuthorServiceImpl(
            AuthorMapper authorMapper,
            ImageMapper imageMapper,
            TagMapper tagMapper) {
        super(imageMapper);
        this.authorMapper = authorMapper;
        tagServiceImpl = new TagServiceImpl(tagMapper);
    }

    @Override
    public Author getAuthorById(String id) {
        try {

            // Validate the existence of the author using its ID and return the article if
            // it exists.
            validateAuthor(id);

            // Retrieve the list of tags associated with the article
            List<String> authorInterest = tagServiceImpl.getAuthorInterest(id);

            Author author = authorMapper.findById(id);

            // Set the retrieved tags to the article
            author.setInterests(authorInterest);

            // Return the article with its associated tags
            return author;

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
    public Author addAuthor(Author author, MultipartFile image) {

        try {
            validateEmail(author.getEmail());
            List<String> interestNames = author.getInterests();

            List<Tag> interests = tagServiceImpl.addTags(interestNames);

            Optional<Author> authorExist = authorMapper.findByEmailOrUsername(author.getEmail());

            if (authorExist.isPresent())
                throw new BadRequest("Author already exist.");

            Optional<Author> usernameExist = authorMapper.findByEmailOrUsername(author.getUsername());
            if (usernameExist.isPresent())
                throw new Conflict("Username already exist.");

            author.setPassword(encoder.encode(author.getPassword()));

            Author createdAuthor = authorMapper.addAuthor(author);
            createdAuthor.setInterests(interestNames);

            // Add author's image
            addImage(image, createdAuthor.getId(), "author");

            // Add author interest
            tagServiceImpl.addAuthorInterest(createdAuthor.getId(), interests);

            return createdAuthor;
        } catch (BadRequest | Conflict e) {
            log.error("Bad Request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while creating an account.");

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
            Optional.ofNullable(author.getAddress()).ifPresent(existingAuthor::setAddress);
            Optional.ofNullable(author.getMsisdn()).ifPresent(existingAuthor::setMsisdn);
            Optional.ofNullable(author.getBiography()).ifPresent(existingAuthor::setBiography);

            // Set the current timestamp as the updated date for the article
            LocalDateTime currentDate = LocalDateTime.now();
            existingAuthor.setUpdatedAt(currentDate);

            // Update Author
            authorMapper.editAuthor(existingAuthor);

            // Update the author's interests with the new list and return the updated
            // interest names
            List<String> interestNames = tagServiceImpl.editAuthorInterest(id, author.getInterests());

            // Set the updated tags to the existing author
            existingAuthor.setInterests(interestNames);

            return existingAuthor;
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
    public Map<String, Object> followAuthor(String follower, String author) {
        try {

            validateAuthorFollower(follower, author);
            Map<String, Object> data = authorMapper.getSingleAuthorFollower(
                    follower, author);

            if (data != null)
                throw new Conflict("Author with this id: '"
                        + follower + "' is already following the author with this id: '"
                        + author + "'");

            return authorMapper.followAuthor(follower, author);

        } catch (BadRequest | Conflict | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError("An unexpected error occurred while add the author follower.");
        }
    }

    @Override
    public Map<String, Object> unfollowAuthor(String follower, String author) {
        try {
            validateAuthorFollower(follower, author);
            return authorMapper.unfollowAuthor(follower, author);
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

        } catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the author followers.");
        }
    }

    @Override
    public List<Author> getFollowedAuthors(String id) {

        try {
            validateAuthor(id);
            return authorMapper.getFollowedAuthors(id);
        } catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the followed authors.");
        }
    }

    private void validateAuthorFollower(String followerAuthor, String followedAuthor) {
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

    // TODO - VALIDATE MSISDN
}
