package com.fabdev.AlbirLeaks.jobs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobsController {
    private JobsService service;

    public JobsController(JobsService service) {
        this.service = service;
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs (){
        return service.getJobs();
    }

    @PostMapping("/jobs")
    public Job createNewJob(@RequestBody Job newJob){
        Job job = service.createJob(newJob);
        return job;
    }
}
