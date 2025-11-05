package com.example.carrie.jobs;


import com.example.carrie.services.impl.ArticleServiceImpl;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SingleArticlePublishJob implements Job {

    @Autowired
    private ArticleServiceImpl articleServiceImpl;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String articleId = dataMap.getString("articleId");

        System.out.println("🕒 Publishing scheduled article ID: " + articleId);
        articleServiceImpl.publishArticle(articleId);
    }
}
