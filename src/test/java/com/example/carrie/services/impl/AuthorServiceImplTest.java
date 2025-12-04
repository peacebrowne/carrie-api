package com.example.carrie.services.impl;

import com.example.carrie.dto.AuthorDto;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.mappers.TagMapper;
import com.example.carrie.models.Author;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    AuthorMapper authorMapper;

    @Mock
    TagServiceImpl tagService;

    @InjectMocks
    @Spy
    AuthorServiceImpl authorService;

    @Test
    void getAuthorById_returnsAuthorDto_withInterests(){
        String authorId = "021354ab-fb41-4f36-bccc-a1ad70b20bba";
        Author author = new Author();
        author.setId(authorId);

        Mockito.when(authorMapper.findById(authorId)).thenReturn(author);

        AuthorDto result = authorService.getAuthorById(authorId);

        Assertions.assertEquals(authorId, result.getId());
    }
}