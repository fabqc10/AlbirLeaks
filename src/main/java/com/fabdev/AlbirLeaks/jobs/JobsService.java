package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class JobsService {

    private Logger logger = LoggerFactory.getLogger(JobsService.class);

    private List<Job> jobs = new ArrayList<>(List.of(
            new Job("J101", "Software Engineer", "Develop software.", "New York", "Tech Corp", LocalDate.now()),
            new Job("J102", "Data Scientist", "Analyze data.", "San Francisco", "Data Inc",LocalDate.now()),
            new Job("J103", "Product Manager", "Manage product.", "Los Angeles", "Product LLC", LocalDate.now())
    ));

    public List<Job> getJobs(){
        return jobs;
    }

    public ResponseJobDTO createJob(CreateJobDTO dto){
        Job newJob = new Job(
                UUID.randomUUID().toString(),
                dto.jobTitle(),
                dto.jobDescription(),
                dto.location(),
                dto.companyName(),
                LocalDate.now()
                );

        jobs.add(newJob);
        return new ResponseJobDTO(
                newJob.getJobId(),
                newJob.getJobTitle(),
                newJob.getJobDescription(),
                newJob.getLocation(),
                newJob.getCompanyName(),
                newJob.getCreatedAt()
        );
    }

    public ResponseJobDTO updateJob(String jobId, UpdateJobDTO dto){
        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
        Job jobToUpdate = jobs.stream().filter(predicate).findFirst().orElseThrow();
        logger.info("dto from SERVICE1: {}",jobToUpdate);


        if (jobToUpdate == null) throw new RuntimeException("Job not found");

        jobToUpdate.setJobTitle(dto.jobTitle());
        jobToUpdate.setJobDescription(dto.jobDescription());
        jobToUpdate.setLocation(dto.location());
        jobToUpdate.setCompanyName(dto.companyName());
        logger.info("dto from SERVICE2: {}",jobToUpdate);


        return new ResponseJobDTO(jobId,jobToUpdate.getJobTitle(),jobToUpdate.getJobDescription(),jobToUpdate.getLocation(),jobToUpdate.getCompanyName(),jobToUpdate.getCreatedAt());
    }

    public void deleteJob(String jobId){
        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
        Job jobToDelete = jobs.stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(()-> new RuntimeException(String.format("Job not found for ID: {}",jobId)));
        jobs.remove(jobToDelete);
    }



}
