package com.fabdev.AlbirLeaks.jobs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class JobsService {

    private List<Job> jobs = new ArrayList<>(List.of(
            new Job("J101", "Software Engineer", "Develop software.", "New York", "Tech Corp"),
            new Job("J102", "Data Scientist", "Analyze data.", "San Francisco", "Data Inc"),
            new Job("J103", "Product Manager", "Manage product.", "Los Angeles", "Product LLC")
    ));

    public List<Job> getJobs(){
        return jobs;
    }

    public Job createJob(Job job){
        Job newJob = new Job(
                job.getJobId(),
                job.getJobTitle(),
                job.getJobDescription(),
                job.getLocation(),
                job.getCompanyName());

        jobs.add(newJob);
        return newJob;
    }

}
