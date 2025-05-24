package com.fabdev.AlbirLeaks.conversation.repository;

import com.fabdev.AlbirLeaks.conversation.model.UserConversationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConversationMetadataRepository extends JpaRepository<UserConversationMetadata, Long> {
    Optional<UserConversationMetadata> findByUser_UserIdAndConversation_Id(String userId, Long conversationId);
} 