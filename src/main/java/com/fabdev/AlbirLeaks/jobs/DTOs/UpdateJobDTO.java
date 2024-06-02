package com.fabdev.AlbirLeaks.jobs.DTOs;

public record UpdateJobDTO(String jobTitle,
                           String jobDescription,
                           String location,
                           String companyName) {
}
