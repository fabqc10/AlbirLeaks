package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.exception.JobNotFoundException;
import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.OwnerDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import com.fabdev.AlbirLeaks.jobs.mappers.JobMapper;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class JobsService {

    private UserService userService;
    private final ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(JobsService.class);

    public JobsService(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    private List<Job> jobs = new ArrayList<>();

    public List<ResponseJobDTO> getJobs(){
        return jobs.stream().map(JobMapper::mapToResponseJobDTO).collect(Collectors.toList());
    }


    public ResponseJobDTO getJobById(String jobId) {
        Predicate<Job> jobPredicate = job -> job.getJobId().equals(jobId);
        Job jobToFind = jobs.stream().filter(jobPredicate).findFirst().orElse(null);
        if (jobToFind == null) throw new JobNotFoundException(String.format("Job with id: %s not found",jobId));
        User owner = jobToFind.getOwner();
        OwnerDTO ownerDTO = new OwnerDTO(
                owner.getUserId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getRole(),
                owner.getGoogleId());

        return new ResponseJobDTO(
                jobToFind.getJobId(),
                jobToFind.getJobTitle(),
                jobToFind.getJobDescription(),
                jobToFind.getLocation(),
                jobToFind.getCompanyName(),
                jobToFind.getCreatedAt(),
                ownerDTO
        );
    }

    public ResponseJobDTO createJob(CreateJobDTO dto, String googleId) {
        User user = userService.findByGoogleId(googleId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job newJob = new Job(
                UUID.randomUUID().toString(),
                dto.jobTitle(),
                dto.jobDescription(),
                dto.location(),
                dto.companyName(),
                LocalDate.now()
        );

        newJob.setOwner(user);
        jobs.add(newJob);

        user.getJobs().add(newJob);

        OwnerDTO ownerDTO = new OwnerDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getGoogleId());

        return new ResponseJobDTO(
                newJob.getJobId(),
                newJob.getJobTitle(),
                newJob.getJobDescription(),
                newJob.getLocation(),
                newJob.getCompanyName(),
                newJob.getCreatedAt(),
                ownerDTO
        );
    }



//    public ResponseJobDTO updateJob(String jobId, UpdateJobDTO dto){
//        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
//        Job jobToUpdate = jobs.stream().filter(predicate).findFirst().orElse(null);
//
//
//        if (jobToUpdate == null) throw new RuntimeException("Job not found");
//
//        jobToUpdate.setJobTitle(dto.jobTitle());
//        jobToUpdate.setJobDescription(dto.jobDescription());
//        jobToUpdate.setLocation(dto.location());
//        jobToUpdate.setCompanyName(dto.companyName());
//
//
//        return new ResponseJobDTO(jobId,jobToUpdate.getJobTitle(),jobToUpdate.getJobDescription(),jobToUpdate.getLocation(),jobToUpdate.getCompanyName(),jobToUpdate.getCreatedAt());
//    }

    public void deleteJob(String jobId){
        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
        Job jobToDelete = jobs.stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(()-> new RuntimeException(String.format("Job not found for ID: %s",jobId)));
        jobs.remove(jobToDelete);
    }



}
