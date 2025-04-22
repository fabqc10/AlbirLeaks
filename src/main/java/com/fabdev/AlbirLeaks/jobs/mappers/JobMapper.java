package com.fabdev.AlbirLeaks.jobs.mappers;

import com.fabdev.AlbirLeaks.jobs.DTOs.JobSummaryDto;
import com.fabdev.AlbirLeaks.jobs.DTOs.OwnerDTO;
import com.fabdev.AlbirLeaks.jobs.DTOs.ResponseJobDTO;
import com.fabdev.AlbirLeaks.jobs.Job;
import com.fabdev.AlbirLeaks.users.User;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    public static ResponseJobDTO mapToResponseJobDTO(Job job) {
        User owner = job.getOwner();
        OwnerDTO ownerDTO = new OwnerDTO(
                owner.getUserId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getRole(),
                owner.getGoogleId()
        );

        return new ResponseJobDTO(
                job.getJobId(),
                job.getJobTitle(),
                job.getJobDescription(),
                job.getLocation(),
                job.getCompanyName(),
                job.getCreatedAt(),
                ownerDTO
        );
    }

    public static JobSummaryDto toJobSummaryDto(Job job) {
        if (job == null) return null;
        // Asegúrate que Job tiene getJobId() y getJobTitle() y que getJobId() devuelve el tipo correcto (String/Long)
        return new JobSummaryDto(job.getJobId(), job.getJobTitle());
    }

}
