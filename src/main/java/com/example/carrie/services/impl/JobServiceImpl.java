package com.example.carrie.services.impl;

import com.example.carrie.jobs.SingleArticlePublishJob;
import com.example.carrie.services.JobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);


    @Override
    public Date scheduleArticlePublish(String articleId, LocalDateTime publishTime) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(SingleArticlePublishJob.class)
                    .withIdentity("publishJob-" + articleId, "article-jobs")
                    .usingJobData("articleId", articleId)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + articleId, "article-triggers")
                    .startAt(Date.from(publishTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            log.info("Scheduled article: {} to publish at {}", articleId, publishTime);
            return scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            log.error(
                    "Internal Server Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule article publish job", e);
        }
    }

}
