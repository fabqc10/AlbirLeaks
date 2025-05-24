package com.fabdev.AlbirLeaks.conversation;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

// Clase para la clave primaria compuesta de ConversationParticipant
@Embeddable // Indica que esta clase se puede incrustar en otra entidad
public class ConversationParticipantId implements Serializable {

    private Long conversationId; // Coincide con el tipo de ID de Conversation
    private String userId;      // Coincide con el tipo de ID de User (UUID String)

    // Constructor vacío requerido por JPA
    public ConversationParticipantId() {
    }

    public ConversationParticipantId(Long conversationId, String userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    // Getters (Setters opcionales si se usan constructores)
    public Long getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
    }

    // --- equals() y hashCode() OBLIGATORIOS para claves compuestas ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversationParticipantId that = (ConversationParticipantId) o;
        return Objects.equals(conversationId, that.conversationId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
} 