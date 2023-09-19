package com.linking.chatroom.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linking.chat.dto.ResType;
import com.linking.chatroom_badge.domain.ChatRoomBadge;
import com.linking.chatroom_badge.persistence.ChatRoomBadgeRepository;
import com.linking.global.common.ChattingSession;
import com.linking.participant.domain.Participant;
import com.linking.participant.dto.ChatRoomFocusingParticipantRes;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ChatRoomManager {

    private final Long projectId;
    private final ChatRoom chatRoom;
    private final List<ChattingSession> chattingSessionList;

    public ChatRoomManager(ChatRoom chatRoom){
        this.projectId = chatRoom.getProject().getProjectId();
        this.chatRoom = chatRoom;
        chattingSessionList = new ArrayList<>();
    }

    public void sendTextChatMessage(ObjectMapper objectMapper, ChatRoomBadgeRepository chatRoomBadgeRepository, ResType resType, Object object) throws RuntimeException {
        List<ChattingSession> notFocusingSessionList = chattingSessionList.stream().filter(c -> !c.getIsFocusing()).collect(Collectors.toList());
        List<Long> notFocusingParticipantIdList = notFocusingSessionList.stream()
                .map(ChattingSession::getParticipant).map(Participant::getParticipantId).collect(Collectors.toList());
        List<Long> focusingParticipantIdList = chattingSessionList.stream().filter(ChattingSession::getIsFocusing)
                        .map(ChattingSession::getParticipant).map(Participant::getParticipantId).collect(Collectors.toList());

        chattingSessionList.stream().filter(ChattingSession::getIsFocusing).forEach(cs -> {
            try {
                sendTextMessage(cs, getReponseTextMessage(objectMapper, resType, object));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        sendChatRoomBadge(objectMapper, notFocusingSessionList,
                plusChatRoomBadgeListCount(chatRoomBadgeRepository, focusingParticipantIdList, notFocusingParticipantIdList));
    }

    public void deleteChattingSession(WebSocketSession webSocketSession) {
        for(ChattingSession cs : chattingSessionList)
            if(cs.getWebSocketSession().getId().equals(webSocketSession.getId())) {
                chattingSessionList.remove(cs);
                break;
            }
    }

    public void setChattingSessionFocusState(ChatRoomBadgeRepository chatRoomBadgeRepository, WebSocketSession session, boolean isFocusing){
        for(ChattingSession cs : chattingSessionList){
            if(cs.getWebSocketSession().getId().equals(session.getId())) {
                cs.setIsFocusing(isFocusing);
                if(isFocusing)
                    resetChatRoomBadge(chatRoomBadgeRepository, cs.getParticipant());
                break;
            }
        }
    }

    public void sendFocusingUsers(ObjectMapper objectMapper) throws RuntimeException {
        chattingSessionList.forEach(cs -> {
            try {
                sendTextMessage(cs, getReponseTextMessage(objectMapper, ResType.userList, getFocusingUsers()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private TextMessage getReponseTextMessage (ObjectMapper objectMapper, ResType resType, Object object) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("resType", resType);
        map.put("data", object);
        return new TextMessage(objectMapper.writeValueAsString(map));
    }

    public void sendTextMessage(ChattingSession chattingSession, ObjectMapper objectMapper, ResType resType, Object object) throws JsonProcessingException {
        sendTextMessage(chattingSession, getReponseTextMessage(objectMapper, resType, object));
    }

    public void sendTextMessage(ChattingSession chattingSession, TextMessage textMessage) {
        try {
            chattingSession.getWebSocketSession().sendMessage(textMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ChatRoomBadge> plusChatRoomBadgeListCount(
            ChatRoomBadgeRepository chatRoomBadgeRepository, List<Long> focusingIds, List<Long> notFocusingIds){
        List<ChatRoomBadge> savedBadgeList = new ArrayList<>();
        List<ChatRoomBadge> chatRoomBadgeList = chatRoomBadgeRepository.findChatRoomBadgesByChatRoom(chatRoom);
        for (ChatRoomBadge chatRoomBadge : chatRoomBadgeList) {
            if(notFocusingIds.contains(chatRoomBadge.getParticipant().getParticipantId())){
                chatRoomBadge.plusCount();
                savedBadgeList.add(chatRoomBadge);
            }
            else if(!focusingIds.contains(chatRoomBadge.getParticipant().getParticipantId()))
                chatRoomBadge.plusCount();
        }
        chatRoomBadgeRepository.saveAll(chatRoomBadgeList);
        return savedBadgeList;
    }

    private void sendChatRoomBadge(ObjectMapper objectMapper, List<ChattingSession> notFocusingSessionList, List<ChatRoomBadge> chatRoomBadgeList){
        for(ChattingSession cs: notFocusingSessionList) {
            try {
                int num = chatRoomBadgeList.stream()
                        .filter(c -> c.getParticipant().getParticipantId().equals(cs.getParticipant().getParticipantId())).findAny()
                        .orElseThrow(NoSuchElementException::new).getUnreadCount();
                sendTextMessage(cs, getReponseTextMessage(objectMapper, ResType.badgeAlarm, num));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<ChatRoomFocusingParticipantRes> getFocusingUsers(){
        List<ChattingSession> focusingSessionList = chattingSessionList.stream()
                .filter(ChattingSession::getIsFocusing).collect(Collectors.toList());
        List<Participant> participantList = focusingSessionList.stream()
                .map(ChattingSession::getParticipant).collect(Collectors.toList());

        List<ChatRoomFocusingParticipantRes> resList = new ArrayList<>();
        for(Participant participant : participantList){
            resList.add(new ChatRoomFocusingParticipantRes(participant.getUserName()));
        }
        return resList;
    }

    private void resetChatRoomBadge(ChatRoomBadgeRepository chatRoomBadgeRepository, Participant participant){
        ChatRoomBadge chatRoomBadge = chatRoomBadgeRepository.findChatRoomBadgeByParticipant(participant).orElseThrow(NoSuchElementException::new);
        chatRoomBadge.resetCnt();
        chatRoomBadgeRepository.save(chatRoomBadge);
    }

}
