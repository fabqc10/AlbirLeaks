package com.fabdev.AlbirLeaks.users.dto;


import lombok.Value; // O usa getters/constructores si no usas Lombok

// DTO simple para devolver el ID de DB y el rol
@Value // Lombok: crea constructor, getters, final fields, equals/hashcode, toString
public class BackendUserDto {
    String userId; // El UUID de la base de datos
    String role;
}