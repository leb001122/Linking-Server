package com.linking.socket.page.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PageSocketSessionRepositoryImpl {

    /**
     * key : pageId
     */
    private final Map<Long, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public int save(Long key, WebSocketSession session) {

        Set<WebSocketSession> sessionsByPage = sessions.get(key);

        if (sessionsByPage == null) {
            sessionsByPage = Collections.synchronizedSet(new HashSet<>());
            sessionsByPage.add(session);
            sessions.put(key, sessionsByPage);
        } else {
            sessionsByPage.add(session);
        }
        return sessionsByPage.size();
    }

    public Set<WebSocketSession> findByPageId(Long key) {
        return sessions.get(key);
    }

    public void remove(Long key, WebSocketSession session) {
        Set<WebSocketSession> sessionByKey = sessions.get(key);
        if (sessionByKey != null)
            sessionByKey.remove(session);
    }

    public Map<Long, Set<WebSocketSession>> getAll() {
        return sessions;
    }
}
