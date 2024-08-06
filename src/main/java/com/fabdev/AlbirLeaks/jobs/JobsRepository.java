package com.fabdev.AlbirLeaks.jobs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsRepository extends JpaRepository<Job,String> {
    List<Job> findByOwnerGoogleId(String googleId);
}
