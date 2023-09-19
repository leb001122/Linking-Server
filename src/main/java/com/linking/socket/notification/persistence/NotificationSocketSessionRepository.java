package com.linking.socket.notification.persistence;

import com.linking.socket.notification.PushWebSocketSession;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationSocketSessionRepository {

    /**
     * key : userId
     */
    private final Map<Long, Set<PushWebSocketSession>> sessions = new ConcurrentHashMap<>();

    public int save(Long key, PushWebSocketSession session) {

        Set<PushWebSocketSession> sessionsByPage = sessions.get(key);

        if (sessionsByPage == null) {
            sessionsByPage = Collections.synchronizedSet(new HashSet<>());
            sessionsByPage.add(session);
            sessions.put(key, sessionsByPage);
        } else {
            sessionsByPage.add(session);
        }
        return sessionsByPage.size();
    }

    public Set<PushWebSocketSession> findByUserId(Long key) {
        return sessions.get(key);
    }

    public Map<Long, Set<PushWebSocketSession>> getAll() {
        return sessions;
    }

    public void remove(Long key, WebSocketSession session) {
        Set<PushWebSocketSession> sessionsByKey = sessions.get(key);
        if (sessionsByKey != null) {
            sessionsByKey.forEach(se -> {
                if (se.getWebSocketSession().getId().equals(session.getId())) {
                    sessionsByKey.remove(se);
                }
            });
        }
    }
}
