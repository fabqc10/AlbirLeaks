package com.fabdev.AlbirLeaks.conversation.mappers;

import com.fabdev.AlbirLeaks.conversation.Conversation;
import com.fabdev.AlbirLeaks.conversation.dto.ConversationSummaryDto;
import com.fabdev.AlbirLeaks.conversation.dto.JobOwnerInfoDto;
import com.fabdev.AlbirLeaks.jobs.Job;
import com.fabdev.AlbirLeaks.jobs.mappers.JobMapper;
import com.fabdev.AlbirLeaks.message.Message;
import com.fabdev.AlbirLeaks.message.mappers.MessageMapper;
import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.mappers.UserMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConversationMapper {

    public static ConversationSummaryDto toConversationSummaryDto(Conversation conversation) {
        if (conversation == null) return null;

        List<Message> messages = conversation.getMessages();
        Message lastMessage = null;
        if (messages != null && !messages.isEmpty()) {
            Optional<Message> lastMessageOpt = messages.stream()
                    .max(Comparator.comparing(Message::getTimestamp));
            if (lastMessageOpt.isPresent()) {
                lastMessage = lastMessageOpt.get();
            }
        }

        JobOwnerInfoDto ownerInfo = null;
        Job job = conversation.getJob();
        if (job != null) {
            User owner = job.getOwner();
            if (owner != null) {
                ownerInfo = new JobOwnerInfoDto();
                ownerInfo.setUserId(owner.getUserId());
                ownerInfo.setUsername(owner.getUsername());
            }
        }

        int unreadCount = 0;

        return new ConversationSummaryDto(
                conversation.getId(),
                ownerInfo,
                JobMapper.toJobSummaryDto(job),
                conversation.getParticipants().stream()
                        .map(UserMapper::toUserSummaryDto)
                        .collect(Collectors.toList()),
                MessageMapper.toMessageDto(lastMessage),
                conversation.getLastUpdatedAt(),
                unreadCount
        );
    }
}