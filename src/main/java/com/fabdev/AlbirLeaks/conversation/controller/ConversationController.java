package com.fabdev.AlbirLeaks.conversation.controller;

import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.conversation.service.ConversationService;
import com.fabdev.AlbirLeaks.exception.JobNotFoundException;
import com.fabdev.AlbirLeaks.exception.UnauthorizedException;
import com.fabdev.AlbirLeaks.message.dto.MessageDto;
import com.fabdev.AlbirLeaks.message.dto.SendMessageRequestDto;
import com.fabdev.AlbirLeaks.message.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importar Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken; // Para verificar tipo
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken; // Para verificar tipo
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // Ajusta según necesidad
public class ConversationController {

    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;
    private final MessageService messageService;
    // Ya no se necesita UserService aquí

    // Helper para extraer googleId (claim 'sub') de diferentes tipos de Authentication
    private Optional<String> extractGoogleId(Authentication authentication) {
        if (authentication == null) {
            log.trace("Authentication object is null.");
            return Optional.empty();
        }
        String googleId = null;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            googleId = oauthToken.getPrincipal().getAttribute("sub");
            log.trace("Extracted googleId from OAuth2AuthenticationToken (sub claim): {}", googleId);
        } else if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            googleId = jwtToken.getToken().getClaimAsString("sub");
            log.trace("Extracted googleId from JwtAuthenticationToken (sub claim): {}", googleId);
        } else {
            // Podrías añadir soporte para otros tipos si fuera necesario
            log.warn("Unsupported authentication type: {}. Cannot extract googleId.", authentication.getClass().getName());
        }

        if (googleId == null || googleId.trim().isEmpty()) {
            log.warn("Could not extract valid googleId (sub claim) from authentication principal: {}", authentication.getPrincipal());
            return Optional.empty(); // Devuelve empty si es null o vacío
        }
        return Optional.of(googleId);
    }

    @PostMapping("/job/{jobId}")
    public ResponseEntity<?> getOrCreateConversation(
            @PathVariable String jobId,
            Authentication authentication // Recibir Authentication genérico
    ) {
        log.info("POST /api/conversations/job/{} request received", jobId);
        Optional<String> googleIdOpt = extractGoogleId(authentication);

        if (googleIdOpt.isEmpty()) {
            log.warn("Unauthorized attempt [POST /job/{}] - Could not determine google ID from authentication: {}", jobId, authentication);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required or user identifier missing.");
        }
        String googleId = googleIdOpt.get();
        log.debug("Authenticated google ID: {}", googleId);

        try {
            // Llamar al servicio con googleId (String)
            ConversationSummaryDto conversation = conversationService.getOrCreateConversationForJob(jobId, googleId);
            log.info("Successfully retrieved/created conversation ID: {} for job {} and googleId {}", conversation.id(), jobId, googleId);
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) { // Ej: Conversación consigo mismo
            log.warn("Bad request [POST /job/{}] (googleId: {}): {}", jobId, googleId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JobNotFoundException e) {
            log.warn("Job not found [POST /job/{}] (googleId: {}): {}", jobId, googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) { // Ej: Usuario no encontrado por googleId en el servicio
            log.error("Runtime error [POST /job/{}] (googleId: {}): {}", jobId, googleId, e.getMessage());
            // No exponer detalles internos, pero loguear el error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error processing request.");
        } catch (Exception e) { // Otros errores inesperados
            log.error("Unexpected error [POST /job/{}] (googleId: {}): {}", jobId, googleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserConversations(
            Authentication authentication // Recibir Authentication genérico
    ) {
        log.info("GET /api/conversations request received");
        Optional<String> googleIdOpt = extractGoogleId(authentication);

        if (googleIdOpt.isEmpty()) {
            log.warn("Unauthorized attempt [GET /] - Could not determine google ID from authentication: {}", authentication);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required or user identifier missing.");
        }
        String googleId = googleIdOpt.get();
        log.debug("Authenticated google ID: {}", googleId);

        try {
            // Llamar al servicio con googleId (String)
            List<ConversationSummaryDto> conversations = conversationService.getUserConversations(googleId);
            log.info("Returning {} conversations for googleId {}", conversations.size(), googleId);
            return ResponseEntity.ok(conversations);
        } catch (RuntimeException e) { // Ej: Usuario no encontrado
            log.error("Runtime error [GET /] (googleId: {}): {}", googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error processing request.");
        } catch (Exception e) {
            log.error("Unexpected error [GET /] (googleId: {}): {}", googleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable Long conversationId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication // Recibir Authentication genérico
    ) {
        log.info("GET /api/conversations/{}/messages request received (page={}, size={})",
                conversationId, pageable.getPageNumber(), pageable.getPageSize());
        Optional<String> googleIdOpt = extractGoogleId(authentication);

        if (googleIdOpt.isEmpty()) {
            log.warn("Unauthorized attempt [GET /{}/messages] - Could not determine google ID from authentication: {}", conversationId, authentication);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required or user identifier missing.");
        }
        String googleId = googleIdOpt.get();
        log.debug("Authenticated google ID: {}", googleId);

        try {
            // Llamar al servicio con googleId (String)
            Page<MessageDto> messages = messageService.getMessagesForConversation(conversationId, googleId, pageable);
            log.info("Returning page {} of {} messages for conversation {}", messages.getNumber(), messages.getNumberOfElements(), conversationId);
            return ResponseEntity.ok(messages);
        } catch (UnauthorizedException e) { // Lanzada por el servicio si el usuario no tiene acceso
            log.warn("Forbidden attempt [GET /{}/messages] by googleId {}: {}", conversationId, googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Ej: Usuario no encontrado
            log.error("Runtime error [GET /{}/messages] (googleId: {}): {}", conversationId, googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error processing request.");
        } catch (Exception e) {
            log.error("Unexpected error [GET /{}/messages] (googleId: {}): {}", conversationId, googleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody SendMessageRequestDto messageRequest,
            Authentication authentication // Recibir Authentication genérico
    ) {
        log.info("POST /api/conversations/{}/messages request received", conversationId);
        Optional<String> googleIdOpt = extractGoogleId(authentication);

        if (googleIdOpt.isEmpty()) {
            log.warn("Unauthorized attempt [POST /{}/messages] - Could not determine google ID from authentication: {}", conversationId, authentication);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required or user identifier missing.");
        }
        if (messageRequest == null || messageRequest.content() == null || messageRequest.content().trim().isEmpty()) {
            log.warn("sendMessage rejected [POST /{}/messages]: Empty message content", conversationId);
            return ResponseEntity.badRequest().body("Message content cannot be empty.");
        }

        String googleId = googleIdOpt.get();
        log.debug("Authenticated sender google ID: {}", googleId);

        try {
            // Llamar al servicio con googleId (String)
            MessageDto newMessage = messageService.sendMessage(conversationId, googleId, messageRequest);
            log.info("Successfully sent message ID {} to conversation {} by googleId {}", newMessage.id(), conversationId, googleId);
            // Devolver CREATED con el DTO del mensaje
            return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
        } catch (UnauthorizedException e) { // Lanzada por el servicio si el usuario no tiene acceso
            log.warn("Forbidden attempt [POST /{}/messages] by googleId {}: {}", conversationId, googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Ej: Usuario no encontrado
            log.error("Runtime error [POST /{}/messages] (googleId: {}): {}", conversationId, googleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error processing request.");
        } catch (Exception e) {
            log.error("Unexpected error [POST /{}/messages] (googleId: {}): {}", conversationId, googleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
