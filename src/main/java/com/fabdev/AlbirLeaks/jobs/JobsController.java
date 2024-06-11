package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import com.fabdev.AlbirLeaks.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class JobsController {
    private UserService userService;
    private JobsService service;
    private Logger logger = LoggerFactory.getLogger(JobsController.class);

    public JobsController(UserService userService, JobsService service) {
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs (){
        return service.getJobs();
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseJobDTO getJob(@PathVariable String jobId){
        ResponseJobDTO jobToFind = service.getJobById(jobId);
        return jobToFind;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResponseJobDTO> createNewJob(@RequestBody CreateJobDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String googleId = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("sub");
        ResponseJobDTO job = service.createJob(dto,googleId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{jobId}")
                .buildAndExpand(job.jobId())
                .toUri();
        return ResponseEntity.created(location).body(job);
    }

    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<ResponseJobDTO> updateJob(@PathVariable String jobId, @RequestBody UpdateJobDTO dto){
        ResponseJobDTO updatedJob = service.updateJob(jobId, dto);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable String jobId){
        service.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }


}
