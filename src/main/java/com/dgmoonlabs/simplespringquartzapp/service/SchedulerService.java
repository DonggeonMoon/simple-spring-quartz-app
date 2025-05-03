package com.dgmoonlabs.simplespringquartzapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final Scheduler scheduler;

    public void updateTriggerInterval(final String jobName, final String newCronExpression) throws SchedulerException {
        checkIfValidCronExpression(newCronExpression);

        JobKey jobKey = JobKey.jobKey(jobName, "stockGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "Trigger", "stockGroup");

        checkIfJobKeyExists(jobKey);
        checkIfTriggerKeyExists(triggerKey);

        Trigger newTrigger = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);

        log.info("Schedule has changed into [{}]", newCronExpression);
    }

    private void checkIfValidCronExpression(final String cronExpression) {
        try {
            CronScheduleBuilder.cronSchedule(cronExpression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }
    }

    private void checkIfJobKeyExists(final JobKey jobKey) throws SchedulerException {
        if (!scheduler.checkExists(jobKey)) {
            throw new IllegalArgumentException("Job does not exist: " + jobKey.getName());
        }
    }

    private void checkIfTriggerKeyExists(final TriggerKey triggerKey) throws SchedulerException {
        if (!scheduler.checkExists(triggerKey)) {
            throw new IllegalArgumentException("Trigger does not exist: " + triggerKey.getName());
        }
    }

    public void pauseJob(final String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, "stockGroup");
        scheduler.pauseJob(jobKey);

        log.info("Job has been paused: {}", jobName);
    }

    public void resumeJob(final String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, "stockGroup");
        scheduler.resumeJob(jobKey);

        log.info("Job has been resumed: {}", jobName);
    }
}
