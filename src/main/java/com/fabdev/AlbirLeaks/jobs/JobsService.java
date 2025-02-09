package com.fabdev.AlbirLeaks.jobs;

import com.fabdev.AlbirLeaks.exception.JobNotFoundException;
import com.fabdev.AlbirLeaks.exception.UnauthorizedException;
import com.fabdev.AlbirLeaks.jobs.DTOs.CreateJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.UpdateJobDTO;
import com.fabdev.AlbirLeaks.jobs.mappers.JobMapper;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class JobsService {

    private final JobsRepository jobsRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(JobsService.class);

    public JobsService(JobsRepository jobsRepository, ObjectMapper objectMapper, UserService userService) {
        this.jobsRepository = jobsRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

//    private List<Job> jobs = new ArrayList<>();

//    public List<ResponseJobDTO> getJobs(){
//        return jobs.stream().map(JobMapper::mapToResponseJobDTO).collect(Collectors.toList());
//    }

    public List<ResponseJobDTO> getJobs(){
        List<Job> jobs = jobsRepository.findAll();
        return jobs.stream().map(JobMapper::mapToResponseJobDTO).collect(Collectors.toList());
    }

//    public ResponseJobDTO getJobById(String jobId) {
//        Predicate<Job> jobPredicate = job -> job.getJobId().equals(jobId);
//        Job jobToFind = jobs.stream().filter(jobPredicate).findFirst().orElse(null);
//        if (jobToFind == null) throw new JobNotFoundException(String.format("Job with id: %s not found",jobId));
//        return JobMapper.mapToResponseJobDTO(jobToFind);
//    }

    public List<ResponseJobDTO> getJobsByUser(String googleId){
        System.out.println("in funtion getJobsByUserssssss");
        User user = userService
                .findByGoogleId(googleId)
                .orElseThrow(()->new RuntimeException("User not found in gjbID"));

        List<Job> jobs = jobsRepository.findByOwnerGoogleId(googleId);

        return jobs.stream()
                .map(JobMapper::mapToResponseJobDTO)
                .collect(Collectors.toList());
    }

    public ResponseJobDTO getJobById(String jobId) {
        Job jobToFind = jobsRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(String.format("Job with id: %s not found", jobId)));
        return JobMapper.mapToResponseJobDTO(jobToFind);
    }

    @Transactional
    public ResponseJobDTO createJob(CreateJobDTO dto, String googleId) {
        User user = userService.findByGoogleId(googleId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job newJob = new Job(
                dto.jobTitle(),
                dto.jobDescription(),
                dto.location(),
                dto.companyName(),
                LocalDate.now()
        );

        newJob.setOwner(user);
        jobsRepository.save(newJob);
//        jobs.add(newJob);

        user.getJobs().add(newJob);

        return JobMapper.mapToResponseJobDTO(newJob);
    }

    @Transactional
    public ResponseJobDTO updateJob(String jobId, UpdateJobDTO dto){
        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
        List<Job> jobs = jobsRepository.findAll();
        Job jobToUpdate = jobs.stream().filter(predicate).findFirst().orElse(null);


        if (jobToUpdate == null) throw new RuntimeException("Job not found");

        jobToUpdate.setJobTitle(dto.jobTitle());
        jobToUpdate.setJobDescription(dto.jobDescription());
        jobToUpdate.setLocation(dto.location());
        jobToUpdate.setCompanyName(dto.companyName());

        jobsRepository.save(jobToUpdate);

        return JobMapper.mapToResponseJobDTO(jobToUpdate);
    }
//    @Transactional
//    public ResponseJobDTO updateJob(String jobId, UpdateJobDTO dto) {
//        Job jobToUpdate = jobsRepository.findById(jobId)
//                .orElseThrow(() -> new JobNotFoundException("Job not found"));
//
//        jobToUpdate.setJobTitle(dto.jobTitle());
//        jobToUpdate.setJobDescription(dto.jobDescription());
//        jobToUpdate.setLocation(dto.location());
//        jobToUpdate.setCompanyName(dto.companyName());
//
//        jobsRepository.save(jobToUpdate);
//
//        return JobMapper.mapToResponseJobDTO(jobToUpdate);
//    }



//    public void deleteJob(String jobId, String googleId) {
//        Predicate<? super Job> predicate = job -> job.getJobId().equals(jobId);
//        Job jobToDelete = jobs.stream()
//                .filter(predicate)
//                .findFirst()
//                .orElseThrow(() -> new JobNotFoundException(String.format("Job not found for ID: %s", jobId)));
//
//        if (!jobToDelete.getOwner().getGoogleId().equals(googleId)) {
//            throw new UnauthorizedException("You are not authorized to delete this job");
//        }
//
//        jobs.remove(jobToDelete);
//    }

    @Transactional
    public void deleteJob(String jobId, String googleId) {
        Job jobToDelete = jobsRepository.findById(jobId)
                .orElseThrow(()->new JobNotFoundException(String.format("Job not found for ID: %s", jobId)));

        if (!jobToDelete.getOwner().getGoogleId().equals(googleId)) {
            throw new UnauthorizedException("You are not authorized to delete this job");
        }

        jobsRepository.delete(jobToDelete);
    }

}
