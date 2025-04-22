package com.fabdev.AlbirLeaks.message.dto;


import com.fabdev.AlbirLeaks.users.dto.UserSummaryDto;
import java.time.LocalDateTime;

// DTO para representar un mensaje enviado al frontend
public record MessageDto(Long id, UserSummaryDto sender, String content, LocalDateTime timestamp) {}