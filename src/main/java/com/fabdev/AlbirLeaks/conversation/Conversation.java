package com.fabdev.AlbirLeaks.conversation;

import com.fabdev.AlbirLeaks.jobs.Job; // Verifica que esta ruta es correcta
import com.fabdev.AlbirLeaks.message.Message; // Crea este archivo después
import com.fabdev.AlbirLeaks.users.User; // Verifica que esta ruta es correcta
import com.fabdev.AlbirLeaks.conversation.model.UserConversationMetadata; // Corregir la importación si es necesario, esta es la ruta donde se creó
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el anuncio (Job)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id") // Nombre de la columna en la tabla 'conversations'
    private Job job;

    // Participantes de la conversación
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversation_participants", // Tabla intermedia para la relación muchos-a-muchos
            joinColumns = @JoinColumn(name = "conversation_id"), // Columna que referencia a esta entidad (Conversation)
            inverseJoinColumns = @JoinColumn(name = "user_id") // Columna que referencia a la otra entidad (User)
    )
    @Builder.Default // Para inicializar con Lombok Builder
    private Set<User> participants = new HashSet<>();

    // Mensajes de la conversación
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC") // Ordena los mensajes por fecha
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt; // Se actualiza con cada mensaje nuevo

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserConversationMetadata> userMetadata = new HashSet<>();

    // Lógica para establecer timestamps automáticamente
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // lastUpdatedAt se actualiza explícitamente en el servicio cuando se envía un mensaje
        // o cuando se marca como leído, así que no es necesario aquí para evitar bucles o
        // actualizaciones no deseadas. Si se quiere actualizar siempre, se puede descomentar.
        // this.lastUpdatedAt = LocalDateTime.now();
    }
}