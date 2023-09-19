package com.linking.global.config;

import com.linking.chat.controller.ChattingWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ChattingWebSocketConfig implements WebSocketConfigurer {

    private final ChattingWebSocketHandler chattingWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chattingWebSocketHandler, "ws/chatting")
                .setAllowedOrigins("*");
    }
}