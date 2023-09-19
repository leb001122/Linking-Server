package com.linking.socket.page.persistence;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Component
public interface IPageSocketRepository {

    /**
     * @return size of sessionByPage
     */
    int save(Long pageId, WebSocketSession session);

    Set<WebSocketSession> findByPageId(Long pageId);
}
