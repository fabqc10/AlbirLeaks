package com.fabdev.AlbirLeaks.message.mappers;

import com.fabdev.AlbirLeaks.message.Message;
import com.fabdev.AlbirLeaks.message.dto.MessageDto;
import com.fabdev.AlbirLeaks.users.mappers.UserMapper;

public class MessageMapper {
    public static MessageDto toMessageDto(Message message) {
        if (message == null) return null;
        return new MessageDto(
                message.getId(),
                UserMapper.toUserSummaryDto(message.getSender()), // Convierte el User remitente
                message.getContent(),
                message.getTimestamp()
        );
    }
}
