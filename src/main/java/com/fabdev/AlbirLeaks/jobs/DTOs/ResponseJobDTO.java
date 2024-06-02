package com.fabdev.AlbirLeaks.jobs.DTOs;

import java.time.LocalDate;

public record ResponseJobDTO(String jobId,
                             String jobTitle,
                             String jobDescription,
                             String location,
                             String companyName,
                             LocalDate createdAt) {
}
