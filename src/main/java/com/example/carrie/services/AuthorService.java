package com.example.carrie.services;

import java.util.List;
import java.util.Map;

import com.example.carrie.dto.CustomDto;
import com.example.carrie.models.Author;

public interface AuthorService {

  public Author getAuthorById(String id);

  public Author addAuthor(Author author);

  public List<?> getAllAuthors(String sort, Long limit, Long start);

  public Author editAuthor(Author author, String id);

  public Author deleteAuthor(String id);

  public Map<String, Object> followAuthor(String followerAuthor, String followedAuthor) ;

  CustomDto getAuthorFollowers(String id);
}
