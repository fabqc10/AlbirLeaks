package com.fabdev.AlbirLeaks.jobs.DTOs;

public record OwnerDTO(String userId,
                       String username,
                       String email,
                       String role,
                       String googleId) {
}
