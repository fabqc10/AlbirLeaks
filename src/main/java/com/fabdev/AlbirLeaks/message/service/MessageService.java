package com.fabdev.AlbirLeaks.message.service;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.service.ConversationService; // Usa ConversationService
import com.fabdev.AlbirLeaks.message.Message;
import com.fabdev.AlbirLeaks.message.dto.MessageDto;
import com.fabdev.AlbirLeaks.message.dto.SendMessageRequestDto;
import com.fabdev.AlbirLeaks.message.mappers.MessageMapper;
import com.fabdev.AlbirLeaks.message.repository.MessageRepository;
import com.fabdev.AlbirLeaks.users.User; // Verifica importación
import com.fabdev.AlbirLeaks.users.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ConversationService conversationService; // Necesario para validar acceso y actualizar timestamp
    private final UserService userService; // Necesario para buscar al remitente por googleId

    // Obtiene los mensajes de una conversación (paginados)
    @Transactional(readOnly = true)
    public Page<MessageDto> getMessagesForConversation(Long conversationId, String googleId, Pageable pageable) {
        log.debug("Fetching messages for conversationId: {} and googleId: {}, page: {}, size: {}",
                conversationId, googleId, pageable.getPageNumber(), pageable.getPageSize());
        // Primero, verifica que el usuario (por googleId) tiene acceso a esta conversación
        // Esto también valida que la conversación exista.
        conversationService.findConversationByIdAndUserOrThrow(conversationId, googleId);
        log.trace("Access granted for googleId {} to conversation {}", googleId, conversationId);

        // Si tiene acceso, busca los mensajes paginados
        Page<Message> messagesPage = messageRepository.findByConversationIdOrderByTimestampDesc(conversationId, pageable);
        log.info("Found {} messages on page {} for conversationId: {}", messagesPage.getNumberOfElements(), pageable.getPageNumber(), conversationId);

        // Mapea los mensajes a DTOs
        return messagesPage.map(MessageMapper::toMessageDto);
    }

    // Guarda un nuevo mensaje enviado por un usuario
    @Transactional
    public MessageDto sendMessage(Long conversationId, String senderGoogleId, SendMessageRequestDto requestDto) {
        log.debug("Attempting to send message from googleId: {} to conversationId: {}", senderGoogleId, conversationId);
        // Verifica que el remitente (por googleId) tiene acceso a la conversación
        Conversation conversation = conversationService.findConversationByIdAndUserOrThrow(conversationId, senderGoogleId);
        log.trace("Access granted for sender googleId {} to conversation {}", senderGoogleId, conversationId);

        // Busca la entidad User del remitente usando googleId
        User sender = findUserByGoogleIdOrThrow(senderGoogleId); // Usa el helper con googleId

        // Crea la nueva entidad Message
        Message newMessage = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(requestDto.content())
                // timestamp se establece automáticamente por JPA
                .build();

        // Guarda el mensaje en la BD
        Message savedMessage = messageRepository.save(newMessage);
        log.info("Message saved with ID: {} in conversation ID: {} by user ID: {} (googleId: {})",
                 savedMessage.getId(), conversationId, sender.getGoogleId(), senderGoogleId);

        // Actualiza el timestamp de la conversación para que aparezca primero en la lista
        // Se hace después de guardar el mensaje con éxito
        conversationService.updateConversationTimestamp(conversationId);
        log.trace("Conversation {} timestamp updated.", conversationId);

        // --- PUNTO CLAVE PARA WEBSOCKETS (futuro) ---
        // Aquí es donde emitirías un evento WebSocket a los otros participantes
        // notificando sobre el nuevo mensaje (savedMessage).
        // --- FIN PUNTO CLAVE ---

        // Devuelve el mensaje guardado como DTO
        return MessageMapper.toMessageDto(savedMessage);
    }

    // Método helper para buscar usuario por googleId, lanzando excepción si no existe
    private User findUserByGoogleIdOrThrow(String googleId) {
        log.trace("Finding sender user by googleId: {}", googleId);
        // ¡¡¡ ASEGÚRATE QUE userService.findByGoogleId(String googleId) ESTÁ IMPLEMENTADO Y DEVUELVE Optional<User> !!!
        return userService.findByGoogleId(googleId)
                .orElseThrow(() -> {
                     log.warn("Sender user not found with googleId: {}", googleId);
                     return new RuntimeException("Sender user not found with googleId: " + googleId);
                 });
    }
}