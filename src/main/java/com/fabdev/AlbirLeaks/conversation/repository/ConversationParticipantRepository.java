package com.fabdev.AlbirLeaks.conversation.repository;

import com.fabdev.AlbirLeaks.conversation.ConversationParticipant;
import com.fabdev.AlbirLeaks.conversation.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationParticipantRepository 
    extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    // Método para encontrar una entrada específica por ID de conversación y ID de usuario (UUID String)
    // JPA puede generar este método basado en el nombre si los campos en ConversationParticipantId son correctos
    Optional<ConversationParticipant> findById_ConversationIdAndId_UserId(Long conversationId, String userId);

    // Alternativamente, usando una query JPQL explícita (más seguro si el nombre del método falla)
    /*
    @Query("SELECT cp FROM ConversationParticipant cp WHERE cp.id.conversationId = :conversationId AND cp.id.userId = :userId")
    Optional<ConversationParticipant> findByConversationAndUser(
        @Param("conversationId") Long conversationId, 
        @Param("userId") String userId
    );
    */
} 