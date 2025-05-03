package com.dgmoonlabs.simplespringquartzapp.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class SchedulerRequest {
    private String jobName;
    private String cronExpression;
}
