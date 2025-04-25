package com.fabdev.AlbirLeaks.users.controller;

import com.fabdev.AlbirLeaks.users.UserService;
import com.fabdev.AlbirLeaks.users.dto.BackendUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // Ruta base para este controlador
@RequiredArgsConstructor // Lombok para inyectar UserService
public class UserController {

    private final UserService userService;

    @GetMapping("/by-google/{googleId}") // Endpoint GET para buscar por googleId
    public ResponseEntity<BackendUserDto> getUserByGoogleId(@PathVariable String googleId) {
        return userService.findByGoogleId(googleId) // Llama al servicio existente
                .map(user -> new BackendUserDto(user.getUserId(), user.getRole())) // Mapea User a BackendUserDto
                .map(ResponseEntity::ok) // Si existe, devuelve 200 OK con el DTO
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si no existe, devuelve 404 Not Found
    }

    // Puedes añadir otros endpoints para usuarios aquí si los necesitas
}