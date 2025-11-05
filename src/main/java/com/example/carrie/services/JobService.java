package com.example.carrie.services;

import java.time.LocalDateTime;
import java.util.Date;

public interface JobService {
    public Date scheduleArticlePublish(String articleId, LocalDateTime publishTime);

}
