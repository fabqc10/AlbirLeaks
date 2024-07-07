package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.exception.JobNotFoundException;
import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public List<ResponseJobDTO> getAllJobs() {
        return service.getJobs();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseJobDTO getJob(@PathVariable String jobId) {
        return service.getJobById(jobId);
    }

    @GetMapping(value = "/username")
    public Object currentUserName(Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current Authentication: {}", authentication);
        return authentication.getPrincipal();
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResponseJobDTO> createNewJob(@RequestBody CreateJobDTO dto, Authentication authentication) {
        logger.info("AUTHENTICATION: {}", authentication);

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String googleId = oauthToken.getPrincipal().getAttribute("sub");
            logger.info("Google ID from token: {}", googleId);

            try {
                ResponseJobDTO job = service.createJob(dto, googleId);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{jobId}")
                        .buildAndExpand(job.jobId())
                        .toUri();
                return ResponseEntity.created(location).body(job);
            } catch (Exception e) {
                logger.error("Failed to create job: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.warn("Authentication is not an instance of OAuth2AuthenticationToken");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<ResponseJobDTO> updateJob(@PathVariable String jobId, @RequestBody UpdateJobDTO dto, Authentication authentication) {
        logger.info("AUTHENTICATION: {}", authentication);

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String googleId = oauthToken.getPrincipal().getAttribute("sub");
            logger.info("Google ID from token: {}", googleId);

            try {
                ResponseJobDTO updatedJob = service.updateJob(jobId, dto);
                return ResponseEntity.ok(updatedJob);
            } catch (JobNotFoundException e) {
                logger.error("Job not found: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception e) {
                logger.error("Failed to update job: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.warn("Authentication is not an instance of OAuth2AuthenticationToken");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable String jobId, Authentication authentication) {
        logger.info("AUTHENTICATION: {}", authentication);

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String googleId = oauthToken.getPrincipal().getAttribute("sub");
            logger.info("Google ID from token: {}", googleId);

            try {
                service.deleteJob(jobId,googleId);
                return ResponseEntity.noContent().build();
            } catch (JobNotFoundException e) {
                logger.error("Job not found: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception e) {
                logger.error("Failed to update job: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.warn("Authentication is not an instance of OAuth2AuthenticationToken");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
