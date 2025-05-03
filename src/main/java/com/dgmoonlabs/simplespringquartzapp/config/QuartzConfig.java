package com.dgmoonlabs.simplespringquartzapp.config;

import com.dgmoonlabs.simplespringquartzapp.job.StockPriceMonitorJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail stockPriceMonitorJobDetail() {
        return JobBuilder.newJob(StockPriceMonitorJob.class)
                .withIdentity("stockPriceMonitorJob", "stockGroup")
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    public Trigger stockPriceMonitorJobTrigger(final JobDetail stockPriceMonitorJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(stockPriceMonitorJobDetail)
                .withIdentity("stockPriceMonitorJobTrigger", "stockGroup")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/30 * * ? * MON-FRI")
                                .inTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
                                .withMisfireHandlingInstructionDoNothing()
                )
                .build();
    }
}
