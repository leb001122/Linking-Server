package com.linking.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linking.chatroom.repository.ChatRoomRepository;
import com.linking.chatroom.service.ChatRoomManagerService;
import com.linking.chatroom.domain.ChatRoom;
import com.linking.chat.dto.ChatReq;
import com.linking.chat.service.ChatService;
import com.linking.global.exception.BadRequestException;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.domain.Project;
import com.linking.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChattingWebSocketHandler extends AbstractWebSocketHandler {

    private final String hr = "--------------------------------------------------";
    private final ObjectMapper objectMapper;

    private final ChatService chatService;
    private final ChatRoomManagerService chatRoomManagerService;
    private final ChatRoomRepository chatRoomRepository;

    private final ParticipantRepository participantRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("[ CHATTING ] [ SESSION {} ] CONNECTED {}", session.getId(), hr);
    }

    @Override
    public void handleMessage(@Nonnull WebSocketSession session, @Nonnull WebSocketMessage<?> webSocketMessage) throws Exception {
        log.info("[ CHATTING ] [ SESSION {} ] RAW MESSAGE RECEIVED {}", session.getId(), hr);
        if(webSocketMessage instanceof TextMessage)
            handleTextMessage(session, (TextMessage) webSocketMessage);
        else if(webSocketMessage instanceof BinaryMessage)
            handleBinaryMessage(session, (BinaryMessage) webSocketMessage);
        else {
            log.error("[ CHATTING ] UNREADABLE MESSAGE {}", hr);
            throw new IllegalStateException();
        }
    }

    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session, TextMessage textMessage) throws IOException {
        ChatReq chatReq = objectMapper.readValue(textMessage.getPayload(), ChatReq.class);
        ChatRoom chatRoom = chatRoomRepository
                                .findChatRoomByProject(new Project(chatReq.getProjectId()))
                                .orElseThrow(NoSuchElementException::new);
        Participant participant = participantRepository
                                    .findByUserAndProject(new User(chatReq.getUserId()), chatRoom.getProject())
                                    .orElseThrow(NoSuchElementException::new);
        log.info("[ CHATTING ] [ ROOM {}, USER {} ] MESSAGE {} RECEIVED {}", chatRoom.getChatRoomId(), chatReq.getUserId(), chatReq.getContent(), hr);

        switch(chatReq.getReqType()) {
            case register:
                chatRoomManagerService.registerChattingSessionOnChatRoom(chatRoom, participant, session);
                log.info("[ CHATTING ] [ ROOM {}, USER {} ] REGISTERED {}", chatRoom.getChatRoomId(), chatReq.getUserId(), hr);
                break;

            case open:
                chatRoomManagerService.openChatRoom(chatRoom, session);
                log.info("[ CHATTING ] [ ROOM {}, USER {} ] OPENED {}", chatRoom.getChatRoomId(), chatReq.getUserId(), hr);
                break;

            case text:
                chatRoomManagerService.publishTextMessage(chatRoom, chatService.saveChat(chatRoom, chatReq));
                log.info("[ CHATTING ] [ ROOM {}, USER {} ] MESSAGE SENT {}", chatRoom.getChatRoomId(), chatReq.getUserId(), hr);
                break;

            case close:
                chatRoomManagerService.closeChatRoom(chatRoom, session);
                log.info("[ CHATTING ] [ ROOM {}, USER {} ] CLOSED {}", chatRoom.getChatRoomId(), chatReq.getUserId(), hr);
                break;

            case unregister:
                chatRoomManagerService.unregisterChattingSessionOnChatRoom(chatRoom, session);
                log.info("[ CHATTING ] [ ROOM {}, USER {} ] UNREGISTERED {}", chatRoom.getChatRoomId(), chatReq.getUserId(), hr);
                break;

            case disconnect:
                chatRoomManagerService.disconnectSession(chatRoom, session);
                log.info("[ CHATTING ] [ SESSION {} ] DISCONNECTED {}", session.getId(), hr);
                break;

            default:
                throw new BadRequestException("Request Type Mismatch");
        }
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        chatRoomManagerService.disconnectSession(null, session);
        log.info("[ CHATTING ] [ SESSION {} ] DISCONNECTED {}", session.getId(), hr);
    }

}
