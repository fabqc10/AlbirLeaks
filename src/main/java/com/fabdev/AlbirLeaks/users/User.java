package com.fabdev.AlbirLeaks.users;

import com.fabdev.AlbirLeaks.jobs.Job;
import com.fabdev.AlbirLeaks.conversation.model.UserConversationMetadata;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter // Genera todos los getters
@Setter // Genera todos los setters
@NoArgsConstructor // Genera el constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera un constructor con todos los argumentos (opcional, pero útil)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    private String username;
    private String email;
    private String role;
    private String googleId;

    private String imageUrl;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserConversationMetadata> conversationMetadata;

    public User(String username, String email, String role, String googleId, List<Job> jobs, String imageUrl) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.googleId = googleId;
        this.jobs = jobs;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<UserConversationMetadata> getConversationMetadata() {
        return conversationMetadata;
    }

    public void setConversationMetadata(List<UserConversationMetadata> conversationMetadata) {
        this.conversationMetadata = conversationMetadata;
    }
}
