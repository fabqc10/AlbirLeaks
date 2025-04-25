package com.fabdev.AlbirLeaks.jobs.DTOs;

//Este DTO contiene la información mínima necesaria del propietario del Job
// para mostrar en la lista de conversaciones.
public class JobOwnerInfoDto {
    private String userId; // UUID del propietario
    private String username; // Username del propietario

    // Constructor vacío (puede ser útil)
    public JobOwnerInfoDto() {
    }

    // Getters y Setters
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
}

