package com.linking.chat.service;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chat.domain.Chat;
import com.linking.chat.dto.ChatReq;
import com.linking.chat.dto.ChatRes;
import com.linking.chat.persistence.ChatMapper;
import com.linking.chat.persistence.ChatRepository;
import com.linking.chatroom.repository.ChatRoomRepository;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.domain.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:m:s").withLocale(Locale.KOREAN);
    private final DateTimeFormatter responseFormatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:m").withLocale(Locale.KOREAN);

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final ChatRoomRepository chatRoomRepository;

    private final ParticipantRepository participantRepository;

    public List<ChatRes> getRecentChatList(Long id, Pageable pageable){
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByProject(new Project(id)).orElseThrow(NoSuchElementException::new);
        Page<Chat> messagePage = chatRepository.findMessagesByChatroom(chatRoom, pageable);
        if(messagePage != null && messagePage.hasContent())
            return chatMapper.toRes(responseFormatter, messagePage.getContent());
        return new ArrayList<>();
    }

    public ChatRes saveChat(ChatRoom chatRoom, ChatReq chatReq) {
        return chatMapper.toRes(responseFormatter, chatRepository.save(
                participantRepository.findByUserAndProjectId(chatReq.getUserId(), chatReq.getProjectId())
                        .map(p -> chatMapper.toEntity(requestFormatter, chatReq, p, chatRoom))
                        .orElseThrow(NoSuchElementException::new)));
    }

}
