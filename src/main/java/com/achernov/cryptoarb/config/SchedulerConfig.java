package com.achernov.cryptoarb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

  @Bean
  @Primary
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(6);
    scheduler.setThreadNamePrefix("websocket-scheduler-");
    scheduler.setAwaitTerminationSeconds(10);
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    return scheduler;
  }
}
