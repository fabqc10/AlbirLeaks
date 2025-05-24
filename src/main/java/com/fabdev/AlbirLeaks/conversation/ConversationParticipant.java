package com.fabdev.AlbirLeaks.conversation;

import com.fabdev.AlbirLeaks.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participants") // Nombre de la tabla de unión
@Getter
@Setter
@NoArgsConstructor // Constructor vacío para JPA
public class ConversationParticipant {

    @EmbeddedId // Indica que la clave primaria es una clase incrustada
    private ConversationParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Mapea el campo userId de la clave compuesta (id)
    @JoinColumn(name = "user_id") // Nombre de la columna FK a la tabla users
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId") // Mapea el campo conversationId de la clave compuesta (id)
    @JoinColumn(name = "conversation_id") // Nombre de la columna FK a la tabla conversations
    private Conversation conversation;

    @Column(name = "last_read_timestamp") // Columna para guardar la marca de tiempo
    private LocalDateTime lastReadTimestamp;

    // Constructor útil para crear la relación
    public ConversationParticipant(User user, Conversation conversation) {
        this.user = user;
        this.conversation = conversation;
        this.id = new ConversationParticipantId(conversation.getId(), user.getUserId());
    }

    // equals y hashCode podrían basarse en el ID si es necesario, pero Lombok @Data o @EqualsAndHashCode(onlyExplicitlyIncluded = true) podrían ayudar
    // @Override public boolean equals ...
    // @Override public int hashCode ...
} 