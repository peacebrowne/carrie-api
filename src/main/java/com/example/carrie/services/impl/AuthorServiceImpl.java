package com.example.carrie.services.impl;

import com.example.carrie.entities.Author;
import com.example.carrie.errors.custom.BadRequest;
import com.example.carrie.errors.custom.Conflict;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;

import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.services.AuthorService;
import com.example.carrie.utils.validations.EmailValidator;
import com.example.carrie.utils.validations.UUIDValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {
    private final AuthorMapper authorMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    public AuthorServiceImpl(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    @Override
    public Author getAuthorById(String id) {
        try {

            if (!UUIDValidator.isValidUUID(id))
                throw new BadRequest("Invalid author ID!");

            Optional<Author> author = authorMapper.findById(id);

            if (author.isEmpty())
                throw new NotFound("Author does not exist.");

            return author.get();
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
            String email = author.getEmail();

            validateEmail(email);

            Optional<Author> authorExist = authorMapper.findByEmail(email);

            if (authorExist.isPresent())
                throw new BadRequest("Author already exist.");

            return authorMapper.addAuthor(author);
        } catch (BadRequest e) {
            log.error("Bad Request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while adding the author.");

        }
    }

    @Override
    public Author editAuthor(Author author, String id) {
        try {

            Author existingAuthor = getAuthorById(id);

            if (author.getEmail() != null) {

                validateEmail(author.getEmail());

                if (!Objects.equals(author.getEmail(), existingAuthor.getEmail())) {

                    Optional<Author> authorOptional = authorMapper.findByEmail(author.getEmail());

                    if (authorOptional.isPresent())
                        throw new Conflict("Email is already taken!");

                    existingAuthor.setEmail(author.getEmail());

                }
            }

            Optional.ofNullable(author.getDob()).ifPresent(dob -> existingAuthor.setDob(dob));
            Optional.ofNullable(author.getGender()).ifPresent(gender -> existingAuthor.setGender(gender));
            Optional.ofNullable(author.getUsername()).ifPresent(name -> existingAuthor.setUsername(name));
            Optional.ofNullable(author.getFirstName()).ifPresent(firstName -> existingAuthor.setFirstName(firstName));
            Optional.ofNullable(author.getLastName()).ifPresent(lastName -> existingAuthor.setLastName(lastName));

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

}
