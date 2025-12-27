package com.example.carrie.services.impl;

import com.example.carrie.dto.AuthorDto;
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
import java.util.stream.Collectors;

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
    public AuthorDto getAuthorById(String id) {
        try {

            // Validate the existence of the author using its ID and return the article if it exists.
            validateAuthor(id);

            // Retrieve the list of interests associated with the author
            List<Tag> authorInterestTag = tagServiceImpl.getAuthorInterest(id);
            List<String> authorInterest = authorInterestTag.stream().map(Tag::getName).collect(Collectors.toList());

            Author author = authorMapper.findById(id);

            // Set the retrieved interests to the author
            author.setInterests(authorInterest);

            AuthorDto authorDto = new AuthorDto();

            // Return the author with his/her associated interests
            return authorDto.AuthorDtoMapper(author);

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
    public AuthorDto addAuthor(Author author, MultipartFile image) {

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

            AuthorDto authorDto = new AuthorDto();

            // Add author's image
            addImage(image, createdAuthor.getId(), "author");

            // Add author interest
            tagServiceImpl.addAuthorInterest(createdAuthor.getId(), interests);

            return authorDto.AuthorDtoMapper(author);
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
    public AuthorDto editAuthor(Author author, String id) {
        try {

            AuthorDto existingAuthor = getAuthorById(id);

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
            Optional.ofNullable(author.getFirstName()).ifPresent(existingAuthor::setFirstName);
            Optional.ofNullable(author.getLastName()).ifPresent(existingAuthor::setLastName);
            Optional.ofNullable(author.getUsername()).ifPresent(existingAuthor::setUsername);
            Optional.ofNullable(author.getAddress()).ifPresent(existingAuthor::setAddress);
            Optional.ofNullable(author.getMsisdn()).ifPresent(existingAuthor::setMsisdn);
            Optional.ofNullable(author.getBiography()).ifPresent(existingAuthor::setBiography);

            // Set the current timestamp as the updated date for the article
            LocalDateTime currentDate = LocalDateTime.now();
            existingAuthor.setUpdatedAt(currentDate);

            // Update Author
            authorMapper.editAuthor(existingAuthor);

            // Update the author's interests with the new list and return the updated interest names
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
    public AuthorDto deleteAuthor(String id) {
        try {

            AuthorDto deletedAuthor = getAuthorById(id);
            authorMapper.deleteAuthor(id);
            return deletedAuthor;

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
    public AuthorDto followAuthor(String follower, String author) {
        try {

            validateAuthorFollower(follower, author);
            AuthorDto data = authorMapper.getSingleAuthorFollower(
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
    public AuthorDto unfollowAuthor(String follower, String author) {
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
    public CustomDto getAuthorFollowers(String id, Long limit, Long start) {
        try {
            validateAuthor(id);

            Long total = authorMapper.totalAuthorFollower(id);
            List<AuthorDto> authorFollowers = authorMapper.getAuthorFollowers(id, limit, start);
            return new CustomDto(total, authorFollowers);

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
    public CustomDto getFollowedAuthors(String id, Long limit, Long start) {

        try {
            validateAuthor(id);

            Long total = authorMapper.totalFollowedAuthors(id);
            List<AuthorDto> followedAuthors = authorMapper.getFollowedAuthors(id, limit, start);
            return new CustomDto(total, followedAuthors);
        } catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the followed authors.");
        }
    }

    @Override
    public List<AuthorDto> recommendedAuthors(String authorID, String tagId, Long limit) {
        try {

            validateAuthor(authorID);

            List<AuthorDto> authors = authorMapper.getRecommendedAuthors(authorID, tagId, limit);

            authors.forEach( author -> {
                // Retrieve the list of interests associated with the author
                List<Tag> authorInterestTag = tagServiceImpl.getAuthorInterest(author.getId());
                List<String> authorInterest = authorInterestTag.stream().map(Tag::getName).collect(Collectors.toList());

                // Set the retrieved interest to the author
                author.setInterests(authorInterest);
            });

            // Return the list of authors with his/her associated interests
            return authors;

        }
        catch (BadRequest | NotFound e) {
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the Recommended Authors.");

        }
    }

    public List<AuthorDto> getRecommendedInterestAuthor(String authorId, String tagId, Long limit){
        try{
            validateUUID(tagId);
            validateAuthor(authorId);

            return authorMapper.findRecommendedInterestAuthor(authorId, tagId, limit);

        }catch(BadRequest | NotFound e){
            log.error("Validation Error: {}", e.getMessage(), e);
            throw e;
        }catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the Recommended tags Authors.");

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

        boolean isAuthorExist = authorMapper.isAuthorExist(authorID);
        if (!isAuthorExist) {
            throw new NotFound("Author does not exist!");
        }
    }

    // TODO - VALIDATE MSISDN
}
