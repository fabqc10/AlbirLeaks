package com.fabdev.AlbirLeaks.conversation.dto;

import com.fabdev.AlbirLeaks.jobs.DTOs.JobSummaryDto;
import com.fabdev.AlbirLeaks.message.dto.MessageDto;
import com.fabdev.AlbirLeaks.users.dto.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

// DTO para listar las conversaciones de un usuario
public record ConversationSummaryDto(
        Long id, // ID de la conversación
        JobOwnerInfoDto jobOwnerInfo, // Información del propietario del Job
        JobSummaryDto job, // Info del anuncio relacionado
        List<UserSummaryDto> participants, // Los usuarios en el chat (excluyendo al propio usuario, opcionalmente)
        MessageDto lastMessage, // El último mensaje enviado (para preview)
        LocalDateTime lastUpdatedAt, // Cuándo fue el último mensaje
        int unreadCount // Contador de mensajes no leídos (Placeholder - requiere lógica adicional)
) {}
