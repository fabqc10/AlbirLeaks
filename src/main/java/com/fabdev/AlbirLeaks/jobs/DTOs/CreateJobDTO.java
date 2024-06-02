package com.fabdev.AlbirLeaks.jobs.DTOs;

public record CreateJobDTO(String jobTitle,
                           String jobDescription,
                           String location,
                           String companyName) {
}
