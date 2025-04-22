package com.fabdev.AlbirLeaks.conversation.mappers;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.jobs.mappers.JobMapper; // Usa JobMapper
import com.fabdev.AlbirLeaks.message.Message;
import com.fabdev.AlbirLeaks.message.mappers.MessageMapper; // Usa MessageMapper
import com.fabdev.AlbirLeaks.users.mappers.UserMapper; // Usa UserMapper

import java.time.LocalDateTime; // Importar si no estaba ya
import java.util.Comparator;
import java.util.List; // Importar si no estaba ya
import java.util.Optional; // Importar si no estaba ya
import java.util.stream.Collectors;

public class ConversationMapper {

    public static ConversationSummaryDto toConversationSummaryDto(Conversation conversation) {
        if (conversation == null) return null;

        List<Message> messages = conversation.getMessages(); // Obtener la lista primero
        Message lastMessage = null; // Valor por defecto

        // --- Verificación Segura --- 
        if (messages != null && !messages.isEmpty()) {
            // Solo intenta encontrar el máximo si la lista no es null y no está vacía
            Optional<Message> lastMessageOpt = messages.stream()
                    .max(Comparator.comparing(Message::getTimestamp));
            if (lastMessageOpt.isPresent()) {
                lastMessage = lastMessageOpt.get();
            }
            // Si messages es null o vacío, lastMessage seguirá siendo null
        }
        // --- Fin Verificación --- 

        // Placeholder para contador de no leídos
        int unreadCount = 0; // TODO: Implementar lógica real de no leídos si se necesita

        // Ahora se puede construir el DTO de forma segura
        return new ConversationSummaryDto(
                conversation.getId(),
                JobMapper.toJobSummaryDto(conversation.getJob()), // Convierte el Job asociado
                conversation.getParticipants().stream() // Convierte la lista de participantes
                        .map(UserMapper::toUserSummaryDto)
                        .collect(Collectors.toList()), // Asegúrate que el DTO espera List o cambia a .toSet()
                MessageMapper.toMessageDto(lastMessage), // Convierte el último mensaje (puede ser null)
                conversation.getLastUpdatedAt(),
                unreadCount
        );
    }
}