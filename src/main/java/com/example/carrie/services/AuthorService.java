package com.example.carrie.services;

import java.util.List;

import com.example.carrie.entities.Author;

public interface AuthorService {

  public Author getAuthorById(String id);

  public Author addAuthor(Author author);

  public List<?> getAllAuthors(String sort, Long limit, Long start);

  public Author editAuthor(Author author, String id);

  public Author deleteAuthor(String id);
}
