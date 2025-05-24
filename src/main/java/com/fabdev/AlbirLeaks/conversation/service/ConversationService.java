package com.fabdev.AlbirLeaks.conversation.service;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.conversation.mappers.ConversationMapper;
import com.fabdev.AlbirLeaks.conversation.repository.ConversationRepository;
import com.fabdev.AlbirLeaks.conversation.repository.UserConversationMetadataRepository;
import com.fabdev.AlbirLeaks.conversation.model.UserConversationMetadata;
import com.fabdev.AlbirLeaks.exception.JobNotFoundException;
import com.fabdev.AlbirLeaks.exception.UnauthorizedException;
import com.fabdev.AlbirLeaks.jobs.Job;
import com.fabdev.AlbirLeaks.jobs.JobsRepository;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final JobsRepository jobsRepository;
    private final UserConversationMetadataRepository userConversationMetadataRepository;

    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> getUserConversations(String googleId) {
        log.debug("Fetching conversations for googleId: {}", googleId);
        User user = findUserByGoogleIdOrThrow(googleId);
        String currentUserId = user.getUserId();
        if (currentUserId == null) {
             log.error("Current user (googleId: {}) has a null userId! Cannot calculate unread counts.", googleId);
             currentUserId = "";
        }

        List<Conversation> conversations = conversationRepository.findByParticipantsContainingOrderByLastUpdatedAtDesc(user);
        log.info("Found {} conversations for googleId {}", conversations.size(), googleId);

        String finalCurrentUserId = currentUserId;
        return conversations.stream()
                .map(conv -> {
                    LocalDateTime lastRead = userConversationMetadataRepository
                        .findByUser_UserIdAndConversation_Id(finalCurrentUserId, conv.getId())
                        .map(UserConversationMetadata::getLastReadTimestamp)
                        .orElse(LocalDateTime.MIN);
                    return ConversationMapper.toConversationSummaryDto(conv, finalCurrentUserId, lastRead);
                })
                .collect(Collectors.toList());
    }

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

        String requesterId = requester.getUserId();
        String ownerId = jobOwner.getUserId();

        if (requesterId.equals(ownerId)) {
            log.warn("User (userId: {}) attempted to create conversation with themselves for job ID: {}", requesterId, jobId);
            throw new IllegalArgumentException("Cannot start a conversation with yourself for your own job.");
        }

        Conversation conversation = conversationRepository
                .findByJobIdAndParticipants(jobId, requesterId, ownerId)
                .or(() -> conversationRepository.findByJobIdAndParticipants(jobId, ownerId, requesterId))
                .orElseGet(() -> {
                    log.info("No existing conversation found for job {}, creating new one between users {} and {}", jobId, requesterId, ownerId);
                    return createNewConversation(job, requester, jobOwner);
                 });

        log.info("Returning conversation ID: {} for job {} (requesterId: {})", conversation.getId(), jobId, requesterId);
        markConversationAsRead(conversation.getId(), requesterId);
        return ConversationMapper.toConversationSummaryDto(conversation, requesterId, LocalDateTime.now());
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

    @Transactional
    public void markConversationAsRead(Long conversationId, String userId) {
        log.debug("Marking conversation {} as read for user {}", conversationId, userId);
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        UserConversationMetadata metadata = userConversationMetadataRepository
                .findByUser_UserIdAndConversation_Id(userId, conversationId)
                .orElseGet(() -> {
                    UserConversationMetadata newMetadata = new UserConversationMetadata();
                    newMetadata.setUser(user);
                    newMetadata.setConversation(conversation);
                    return newMetadata;
                });

        metadata.setLastReadTimestamp(LocalDateTime.now());
        userConversationMetadataRepository.save(metadata);
        log.info("Conversation {} marked as read for user {} at {}", conversationId, userId, metadata.getLastReadTimestamp());
    }

    @Transactional(readOnly = true)
    public Conversation findConversationByIdAndUserOrThrow(Long conversationId, String userId) {
        log.debug("Checking access for userId {} to conversationId {}", userId, conversationId);
        return conversationRepository.findByIdAndParticipantId(conversationId, userId)
                .orElseThrow(() -> {
                    log.warn("Unauthorized access attempt by userId {} to conversationId {}", userId, conversationId);
                    return new UnauthorizedException("User " + userId + " not authorized for conversation " + conversationId);
                });
    }

    private User findUserByGoogleIdOrThrow(String googleId) {
        log.trace("Finding user by googleId: {}", googleId);
        return userService.findByGoogleId(googleId)
                .orElseThrow(() -> {
                    log.warn("User not found for googleId: {}", googleId);
                    return new RuntimeException("User not found with googleId: " + googleId);
                });
    }
}