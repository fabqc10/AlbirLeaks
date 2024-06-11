package com.fabdev.AlbirLeaks.users;

import com.fabdev.AlbirLeaks.jobs.Job;

import java.util.List;

public class User {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String googleId;
    private List<Job> jobs;

    public User(String userId, String username, String email, String role, String googleId, List<Job> jobs) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.googleId = googleId;
        this.jobs = jobs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
