package com.linking.socket.page.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linking.global.util.JsonMapper;
import com.linking.socket.page.persistence.PageSocketSessionRepositoryImpl;
import com.linking.socket.page.service.PageWebSocketService;
import com.linking.socket.page.PageSocketMessageReq;
import com.linking.socket.page.TextSendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final PageWebSocketService pageWebSocketService;
    private final PageSocketSessionRepositoryImpl sessionRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long projectId = (Long) session.getAttributes().get("projectId");
        Long pageId = (Long) session.getAttributes().get("pageId");
        Long userId = (Long) session.getAttributes().get("userId");

        log.info("[PAGE_SOCKET] projectId = {} | pageId = {} | userId = {} | session.id = {}", projectId, pageId, userId, session.getId());

        int size = sessionRepository.save(pageId, session);
        log.info("sessions size of page {} is {}", pageId, size);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.close(session);
    }

    private void close(WebSocketSession session) {
        log.info("[PAGE_SOCKET][CLOSE]");
        sessionRepository.remove((Long)session.getAttributes().get("pageId"), session);
        try {
            session.close();
        } catch (IOException e) {
            log.error("PageSocketHandler session.close() IOException");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        PageSocketMessageReq pageSocketMessageReq = null;
        try {
            pageSocketMessageReq = objectMapper.readValue(message.getPayload(), PageSocketMessageReq.class);
        } catch (JsonParseException exception) {
            log.error("TextInputMessage.class 형식에 맞지 않습니다. => {}", exception.getMessage());
        }
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("projectId", session.getAttributes().get("projectId"));
        attributes.put("pageId", session.getAttributes().get("pageId"));
        attributes.put("userId", session.getAttributes().get("userId"));
        attributes.put("sessionId", session.getId());

        pageWebSocketService.handleTextMessage(attributes, pageSocketMessageReq);
    }

    @EventListener
    public void sendEvent(TextSendEvent event) {

        try {
            Set<WebSocketSession> sessions = sessionRepository.findByPageId(event.getPageId());
            if (sessions == null && sessions.isEmpty()) return;

            sessions.forEach(session -> {
                if ((session.getId() != event.getSessionId()) && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(JsonMapper.toJsonString(event.getPageSocketMessageRes())));
                    } catch (IOException e) {
                        log.error("IOException in TextSendEvent -> {}", e.getMessage());
                    }
                }
            });
        } catch (RuntimeException e) {
            log.error("{} in TextSendEvent -> {}", e.getClass(), e.getMessage());
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("pong");
    }

    @Scheduled(fixedRate = 45000)
    public void ping() {
        Map<Long, Set<WebSocketSession>> all = sessionRepository.getAll();
        all.forEach((key, set) -> {
            set.forEach(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new PingMessage());
                        log.info("ping");
                    } catch (IOException e) {
                        this.close(session);
                    }
                } else {
                    this.close(session);
                }
            });
        });
    }
}



