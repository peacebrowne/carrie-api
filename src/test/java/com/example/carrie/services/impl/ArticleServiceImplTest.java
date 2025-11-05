package com.example.carrie.services.impl;

import com.example.carrie.mappers.ArticleMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    JobServiceImpl jobService;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    ArticleServiceImpl articleService;

    @Test
    void scheduleArticleForLaterPublication() {
        String articleId = "01913829-e3c7-449b-ad88-413b97187671";
        String scheduledTime = "2025-11-05 15:15:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(scheduledTime, formatter);
        Date expectedDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        Mockito.when(jobService.scheduleArticlePublish(articleId, dateTime))
                .thenReturn(expectedDate);

        Date scheduleDate = articleService.publishArticleLater(articleId, scheduledTime);

        Assertions.assertEquals(expectedDate, scheduleDate);
    }

}