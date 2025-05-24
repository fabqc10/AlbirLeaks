package com.fabdev.AlbirLeaks.conversation.dto;

// DTO simple para devolver el conteo total de mensajes no leídos.
public class TotalUnreadCountDto {
    private long totalUnread;

    public TotalUnreadCountDto(long totalUnread) {
        this.totalUnread = totalUnread;
    }

    // Getter (Setter opcional, no estrictamente necesario para respuesta)
    public long getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(long totalUnread) {
        this.totalUnread = totalUnread;
    }
} 