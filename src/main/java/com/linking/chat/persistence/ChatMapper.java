package com.linking.chat.persistence;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chat.domain.Chat;
import com.linking.chat.dto.ChatReq;
import com.linking.chat.dto.ChatRes;
import com.linking.participant.domain.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChatMapper {

    default Chat toEntity(DateTimeFormatter formatter, ChatReq chatReq, Participant participant, ChatRoom chatRoom){
        if(chatReq == null || participant == null)
            return null;

        Chat.ChatBuilder chatBuilder = Chat.builder();
        return chatBuilder
                .participant(participant)
                .chatroom(chatRoom)
                .content(chatReq.getContent())
                .sentDatetime(LocalDateTime.parse(chatReq.getSentDatetime(), formatter))
                .build();

    }

    default ChatRes toRes(DateTimeFormatter formatter, Chat chat){
        if(chat == null)
            return null;

        ChatRes.ChatResBuilder chatResBuilder = ChatRes.builder();
        return chatResBuilder
                .firstName(chat.getParticipant().getUser().getFirstName())
                .userName(chat.getParticipant().getUserName())
                .content(chat.getContent())
                .sentDatetime(chat.getSentDatetime().format(formatter))
                .build();
    }

    default List<ChatRes> toRes(DateTimeFormatter formatter, List<Chat> chatList){
        if(chatList == null)
            return null;
        return chatList.stream().map(c -> toRes(formatter, c)).collect(Collectors.toList());
    }

}
