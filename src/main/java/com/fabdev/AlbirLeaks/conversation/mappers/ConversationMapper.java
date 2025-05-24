package com.fabdev.AlbirLeaks.conversation.mappers;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.conversation.dto.JobOwnerInfoDto;
import com.fabdev.AlbirLeaks.jobs.Job;
import com.fabdev.AlbirLeaks.jobs.mappers.JobMapper;
import com.fabdev.AlbirLeaks.message.Message;
import com.fabdev.AlbirLeaks.message.mappers.MessageMapper;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConversationMapper {

    // Acepta el ID del usuario actual (UUID String) y el lastReadTimestamp como parámetros
    public static ConversationSummaryDto toConversationSummaryDto(Conversation conversation, String currentUserId, LocalDateTime lastReadTimestamp) {
        if (conversation == null) return null;

        List<Message> messages = conversation.getMessages();
        Message lastMessage = null;
        if (messages != null && !messages.isEmpty()) {
            Optional<Message> lastMessageOpt = messages.stream()
                    .max(Comparator.comparing(Message::getTimestamp));
            if (lastMessageOpt.isPresent()) {
                lastMessage = lastMessageOpt.get();
            }
        }

        JobOwnerInfoDto ownerInfo = null;
        Job job = conversation.getJob();
        if (job != null) {
            User owner = job.getOwner();
            if (owner != null) {
                ownerInfo = new JobOwnerInfoDto();
                ownerInfo.setUserId(owner.getUserId());
                ownerInfo.setUsername(owner.getUsername());
                ownerInfo.setImageUrl(owner.getImageUrl());
            }
        }

        // --- Cálculo Preciso de UnreadCount --- (ya no aproximado)
        int unreadCount = calculateUnreadCount(conversation, currentUserId, lastReadTimestamp);

        return new ConversationSummaryDto(
                conversation.getId(),
                ownerInfo,
                JobMapper.toJobSummaryDto(job),
                conversation.getParticipants().stream()
                        .map(UserMapper::toUserSummaryDto)
                        .collect(Collectors.toList()),
                MessageMapper.toMessageDto(lastMessage),
                conversation.getLastUpdatedAt(),
                unreadCount // Pasar el valor calculado
        );
    }

    // Calcula cuántos mensajes en la conversación son más recientes que el lastReadTimestamp del usuario actual
    // y no fueron enviados por el usuario actual.
    private static int calculateUnreadCount(Conversation conversation, String currentUserId, LocalDateTime lastReadTimestamp) {
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty() || currentUserId == null) {
            return 0;
        }

        // Si no hay lastReadTimestamp (o es muy antiguo), todos los mensajes recibidos cuentan como no leídos.
        final LocalDateTime readTimestamp = (lastReadTimestamp == null) ? LocalDateTime.MIN : lastReadTimestamp;

        long count = conversation.getMessages().stream()
                .filter(message -> message.getSender() != null && 
                                   !currentUserId.equals(message.getSender().getUserId()) &&
                                   message.getTimestamp().isAfter(readTimestamp))
                .count();
        
        return (int) count;
    }
}