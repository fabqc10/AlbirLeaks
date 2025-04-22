package com.fabdev.AlbirLeaks.conversation.service;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.conversation.mappers.ConversationMapper;
import com.fabdev.AlbirLeaks.conversation.repository.ConversationRepository;
import com.fabdev.AlbirLeaks.exception.JobNotFoundException; // Necesitas crear esta clase
import com.fabdev.AlbirLeaks.exception.UnauthorizedException; // Necesitas crear esta clase
import com.fabdev.AlbirLeaks.jobs.Job; // Verifica importación
import com.fabdev.AlbirLeaks.jobs.JobsRepository;
import com.fabdev.AlbirLeaks.users.User; // Verifica importación
import com.fabdev.AlbirLeaks.users.UserService; // Verifica importación y nombre de tu servicio
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);
    private final ConversationRepository conversationRepository;
    private final UserService userService; // Debe tener findByGoogleId(String) -> Optional<User>
    private final JobsRepository jobsRepository; // Debe tener findById(String) -> Optional<Job>

    // Obtiene todas las conversaciones de un usuario (identificado por su googleId)
    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> getUserConversations(String googleId) {
        log.debug("Fetching conversations for googleId: {}", googleId);
        User user = findUserByGoogleIdOrThrow(googleId);
        List<Conversation> conversations = conversationRepository.findByParticipantsContainingOrderByLastUpdatedAtDesc(user);
        log.info("Found {} conversations for googleId {}", conversations.size(), googleId);
        return conversations.stream()
                .map(ConversationMapper::toConversationSummaryDto)
                .collect(Collectors.toList());
    }

    // Inicia una conversación nueva o devuelve la existente (usuarios por googleId, job por jobId String)
    @Transactional
    public ConversationSummaryDto getOrCreateConversationForJob(String jobId, String requesterGoogleId) {
        log.debug("getOrCreateConversationForJob called for jobId: {} and requesterGoogleId: {}", jobId, requesterGoogleId);
        User requester = findUserByGoogleIdOrThrow(requesterGoogleId);
        Job job = jobsRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", jobId);
                    return new JobNotFoundException("Job not found with id: " + jobId);
                 });

        User jobOwner = job.getOwner();
        if (jobOwner == null) {
            log.error("Job owner is null for job ID: {}", jobId);
            throw new IllegalStateException("Job owner not found for job: " + jobId);
        }

        // Asumiendo que User tiene un método getUserId() que devuelve el googleId (String)
        String requesterId = requester.getUserId();
        String ownerId = jobOwner.getUserId();

        if (requesterId.equals(ownerId)) {
            log.warn("User (googleId: {}) attempted to create conversation with themselves for job ID: {}", requesterGoogleId, jobId);
            throw new IllegalArgumentException("Cannot start a conversation with yourself for your own job.");
        }

        // Usar el String jobId directamente, y los String userIds (googleId)
        // La llamada ahora coincide con la firma del repositorio (String, String, String)
        Conversation conversation = conversationRepository
                .findByJobIdAndParticipants(jobId, requesterId, ownerId)
                .or(() -> conversationRepository.findByJobIdAndParticipants(jobId, ownerId, requesterId))
                .orElseGet(() -> {
                    log.info("No existing conversation found for job {}, creating new one between users {} and {}", jobId, requesterId, ownerId);
                    return createNewConversation(job, requester, jobOwner);
                 });

        log.info("Returning conversation ID: {} for job {} (requesterGoogleId: {})", conversation.getId(), jobId, requesterGoogleId);
        return ConversationMapper.toConversationSummaryDto(conversation);
    }

    private Conversation createNewConversation(Job job, User user1, User user2) {
        Conversation newConversation = Conversation.builder()
                .job(job)
                .participants(new HashSet<>(Set.of(user1, user2)))
                .build();
        return conversationRepository.save(newConversation);
    }

    @Transactional
    public void updateConversationTimestamp(Long conversationId) {
        log.trace("Updating timestamp for conversation ID: {}", conversationId);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    log.warn("Conversation not found for timestamp update: {}", conversationId);
                    return new RuntimeException("Conversation not found: " + conversationId);
                });
        conversation.setLastUpdatedAt(LocalDateTime.now());
    }

    // Busca una conversación asegurándose de que el usuario (por googleId) tiene permiso
    // *** CORRECCIÓN AQUÍ ***
    @Transactional(readOnly = true)
    public Conversation findConversationByIdAndUserOrThrow(Long conversationId, String googleId) {
        log.debug("Checking access for googleId {} to conversationId {}", googleId, conversationId);
        // Llama directamente al método del repositorio que espera (Long, String)
        return conversationRepository.findByIdAndParticipantId(conversationId, googleId)
                .orElseThrow(() -> {
                    log.warn("Unauthorized access attempt by googleId {} to conversationId {}", googleId, conversationId);
                    return new UnauthorizedException("User " + googleId + " not authorized for conversation " + conversationId);
                });
    }

    // Método helper para buscar usuario por googleId (String) y lanzar excepción si no se encuentra
    private User findUserByGoogleIdOrThrow(String googleId) {
        log.trace("Finding user by googleId: {}", googleId);
        return userService.findByGoogleId(googleId)
                .orElseThrow(() -> {
                    log.warn("User not found for googleId: {}", googleId);
                    return new RuntimeException("User not found with id: " + googleId); // Podría ser mejor UserNotFoundException
                });
    }
}