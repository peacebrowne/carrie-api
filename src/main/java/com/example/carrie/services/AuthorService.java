package com.example.carrie.services;

import java.util.List;

import com.example.carrie.dto.AuthorDto;
import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Author;
import org.springframework.web.multipart.MultipartFile;

public interface AuthorService {

  public AuthorDto getAuthorById(String id);

  public AuthorDto addAuthor(Author author, MultipartFile image);

  public List<?> getAllAuthors(String sort, Long limit, Long start);

  public AuthorDto editAuthor(Author author, String id);

  public AuthorDto deleteAuthor(String id);

  public AuthorDto followAuthor(String followerAuthor, String followedAuthor) ;

  public AuthorDto unfollowAuthor(String follower, String author);

  public CustomDto getAuthorFollowers(String id, Long limit, Long start);

  public CustomDto getFollowedAuthors(String id, Long limit, Long start);

  List<AuthorDto> recommendedAuthors(String authorID, String tagId, Long limit);
}
