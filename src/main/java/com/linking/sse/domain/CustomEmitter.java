package com.linking.sse.domain;

import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class CustomEmitter {

    private Long userId;
    private SseEmitter sseEmitter;

    public CustomEmitter(Long userId, SseEmitter sseEmitter) {
        this.userId = userId;
        this.sseEmitter = sseEmitter;
    }
}
