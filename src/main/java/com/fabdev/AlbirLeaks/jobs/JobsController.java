package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobsController {
    private JobsService service;
    private Logger logger = LoggerFactory.getLogger(JobsController.class);

    public JobsController(JobsService service) {
        this.service = service;
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs (){
        return service.getJobs();
    }

    @PostMapping("/jobs")
    public ResponseJobDTO createNewJob(@RequestBody CreateJobDTO dto){
        ResponseJobDTO job = service.createJob(dto);
        return job;
    }

    @PutMapping("/jobs/{jobId}")
    public ResponseJobDTO updateJob(@PathVariable String jobId, @RequestBody UpdateJobDTO dto){
        logger.info("dto from CONTROLLER: {}",dto);
        ResponseJobDTO updatedJob = service.updateJob(jobId, dto);
        return updatedJob;
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable String jobId){
        service.deleteJob(jobId);
        return ResponseEntity.noContent().build();

    }





}
