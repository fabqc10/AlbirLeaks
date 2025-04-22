package com.fabdev.AlbirLeaks.message;

import com.fabdev.AlbirLeaks.conversation.Conversation; // Referencia a la conversación a la que pertenece
import com.fabdev.AlbirLeaks.users.User; // Referencia al usuario que envió el mensaje
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la conversación
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Un mensaje DEBE pertenecer a una conversación
    @JoinColumn(name = "conversation_id", nullable = false) // Clave foránea
    private Conversation conversation;

    // Quién envió el mensaje
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Un mensaje DEBE tener un remitente
    @JoinColumn(name = "sender_id", nullable = false) // Clave foránea
    private User sender;

    // Contenido del mensaje
    @Lob // Para textos largos
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Cuándo se envió
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Establecer timestamp al crear
    @PrePersist
    protected void onSend() {
        timestamp = LocalDateTime.now();
    }
}