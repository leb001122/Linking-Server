package com.linking.socket.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushWebSocketSession {

    @Setter
    private boolean isChecking;
    private WebSocketSession webSocketSession;

    public PushWebSocketSession(WebSocketSession webSocketSession) {
        this.isChecking = false;
        this.webSocketSession = webSocketSession;
    }
}
