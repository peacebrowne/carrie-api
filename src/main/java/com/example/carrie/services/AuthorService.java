package com.example.carrie.services;

import java.util.List;
import java.util.Map;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Author;
import org.springframework.web.multipart.MultipartFile;

public interface AuthorService {

  public Author getAuthorById(String id);

  public Author addAuthor(Author author, MultipartFile image);

  public List<?> getAllAuthors(String sort, Long limit, Long start);

  public Author editAuthor(Author author, String id);

  public Author deleteAuthor(String id);

  public Map<String, Object> followAuthor(String followerAuthor, String followedAuthor) ;

  public Map<String, Object> unfollowAuthor(String follower, String author);

  public CustomDto getAuthorFollowers(String id);

  public List<Author> getFollowedAuthors(String id);

}
