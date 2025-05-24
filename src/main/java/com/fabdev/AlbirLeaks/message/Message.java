package com.fabdev.AlbirLeaks.message;

import com.fabdev.AlbirLeaks.conversation.Conversation; // Referencia a la conversación a la que pertenece
import com.fabdev.AlbirLeaks.users.User; // Referencia al usuario que envió el mensaje
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la conversación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false) // Clave foránea
    private Conversation conversation;

    // Quién envió el mensaje
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false) // Clave foránea
    private User sender;

    // Contenido del mensaje
    @Column(columnDefinition = "TEXT")
    private String content;

    // Cuándo se envió
    private LocalDateTime timestamp;

    // Campo para compatibilidad con unreadCount aproximado anterior si se quiere mantener
    // private boolean read;

    // Establecer timestamp al crear
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}