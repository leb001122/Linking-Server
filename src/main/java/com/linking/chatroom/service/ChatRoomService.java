package com.linking.chatroom.service;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chatroom.repository.ChatRoomRepository;
import com.linking.chatroom_badge.domain.ChatRoomBadge;
import com.linking.chatroom_badge.persistence.ChatRoomBadgeRepository;
import com.linking.participant.domain.Participant;
import com.linking.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomBadgeRepository chatRoomBadgeRepository;

    public void createChatRoom(Project project){
        ChatRoom.ChatRoomBuilder chatRoomBuilder = ChatRoom.builder();
        ChatRoom chatRoom = chatRoomRepository.save(chatRoomBuilder.project(project).build());

        List<ChatRoomBadge> chatRoomBadgeList = new ArrayList<>();
        ChatRoomBadge.ChatRoomBadgeBuilder chatRoomBadgeBuilder = ChatRoomBadge.builder();
        for(int i = 0; i < project.getParticipantList().size(); i++)
            chatRoomBadgeList.add(
                    chatRoomBadgeBuilder
                            .participant(project.getParticipantList().get(i))
                            .chatRoom(chatRoom)
                            .unreadCount(0).build()
            );
        chatRoomBadgeRepository.saveAll(chatRoomBadgeList);
    }

}
