package com.linking.socket.notification.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linking.global.util.JsonMapper;
import com.linking.socket.notification.PushMessageReq;
import com.linking.socket.notification.PushSendEvent;
import com.linking.socket.notification.PushWebSocketSession;
import com.linking.socket.notification.persistence.NotificationSocketSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final NotificationSocketSessionRepository sessionRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long userId = (Long) session.getAttributes().get("userId");

        log.info("[PUSH_SOCKET][OPEN] userId = {}, sessionId = {}", userId, session.getId());

        int size = sessionRepository.save(userId, new PushWebSocketSession(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.close(session);
    }

    private void close(WebSocketSession session) {
        log.info("[PUSH_SOCKET][CLOSE]");
        sessionRepository.remove((Long) session.getAttributes().get("userId"), session);
        try {
            session.close();
        } catch (IOException e) {
            log.error("NotificationSocketHandler session.close() IOException");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // todo isChecking 메시지 전송

        PushMessageReq pushMessageReq = null;
        try {
            pushMessageReq = objectMapper.readValue(message.getPayload(), PushMessageReq.class);
        } catch (JsonParseException exception) {
            log.error("PushMessageReq.class 형식에 맞지 않습니다. => ", exception.getMessage());
        }

        // userId 뽑아서 Set<PushWebSocketSession> 찾아
        // 그중에 세션 id로 일치하는 세션 찾아서 isChecking을 바꿔

        Long userId = (Long) session.getAttributes().get("userId");

        Set<PushWebSocketSession> sessions = sessionRepository.findByUserId(userId);
        for (PushWebSocketSession se : sessions) {
            if (se.getWebSocketSession().getId().equals(session.getId())) {
                se.setChecking(pushMessageReq.getIsChecking());
                log.info("userId = {} noti isChecking = {}", userId, se.isChecking());
                break;
            }
        }
    }


    @EventListener
    public void sendEvent(PushSendEvent event) {
        log.info("send PushSendEvent Message");

        Set<PushWebSocketSession> sessions = sessionRepository.findByUserId(event.getUserId());
        if (sessions == null || sessions.isEmpty()) return;

        if (event.getType().equals("push")) {
            sessions.forEach(session -> {
                if (session.isChecking() && session.getWebSocketSession().isOpen()) {
                    send(session.getWebSocketSession(), event.getData());
                    log.info("send push event");
                }
            });
        } else if (event.getType().equals("badge")) {
            sessions.forEach(session -> {
                if (!session.isChecking() && session.getWebSocketSession().isOpen()) {
                    send(session.getWebSocketSession(), event.getData());
                    log.info("send Badge event");
                }
            });
        }
    }

    public void send(WebSocketSession session, Object data) {
        try {
            session.sendMessage(new TextMessage(JsonMapper.toJsonString(data)));
        } catch (IOException e) {
            log.error("IOException in PushSendEvent -> {}", e.getMessage());
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("pong");
    }

    @Scheduled(fixedRate = 45000)
    public void ping() {
        Map<Long, Set<PushWebSocketSession>> all = sessionRepository.getAll();
        all.forEach((key, set) -> {
            set.forEach(session -> {
                if (session.getWebSocketSession().isOpen()) {
                    try {
                        session.getWebSocketSession().sendMessage(new PingMessage());
                        log.info("ping");
                    } catch (IOException e) {
                        this.close(session.getWebSocketSession());
                    }
                } else {
                    this.close(session.getWebSocketSession());
                }
            });
        });
    }
}
