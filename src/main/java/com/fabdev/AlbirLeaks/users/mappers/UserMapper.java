package com.fabdev.AlbirLeaks.users.mappers;

import com.fabdev.AlbirLeaks.users.User; // Verifica importación
import com.fabdev.AlbirLeaks.users.dto.UserSummaryDto;

public class UserMapper {
    public static UserSummaryDto toUserSummaryDto(User user) {
        if (user == null) return null;
        // Verifica que los métodos getUserId(), getUsername() existen en tu entidad User
        // Pasa null para la imagen ya que no existe en la entidad User actualmente.
        // Asegura que el ID (String) se mapea al DTO que también debería esperar String.
        return new UserSummaryDto(user.getUserId(), user.getUsername(), null);
    }
}
