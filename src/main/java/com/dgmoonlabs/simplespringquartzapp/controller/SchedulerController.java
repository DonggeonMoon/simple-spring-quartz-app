package com.dgmoonlabs.simplespringquartzapp.controller;

import com.dgmoonlabs.simplespringquartzapp.dto.SchedulerRequest;
import com.dgmoonlabs.simplespringquartzapp.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class SchedulerController {
    private final SchedulerService schedulerService;

    @PatchMapping
    public ResponseEntity<Void> updateTrigger(@RequestBody SchedulerRequest request) throws SchedulerException {
        schedulerService.updateTriggerInterval(request.getJobName(), request.getCronExpression());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobName}:pause")
    public ResponseEntity<Void> pauseJob(@PathVariable String jobName) throws SchedulerException {
        schedulerService.pauseJob(jobName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobName}:resume")
    public ResponseEntity<Void> resumeJob(@PathVariable String jobName) throws SchedulerException {
        schedulerService.resumeJob(jobName);
        return ResponseEntity.noContent().build();
    }
}