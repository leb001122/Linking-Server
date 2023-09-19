package com.linking.global.config;

import com.linking.socket.page.handler.PageSocketHandler;
import com.linking.socket.interceptor.PageHandShakeInterceptor;
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
public class PageWebSocketConfig implements WebSocketConfigurer {

    private final PageSocketHandler pageSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(pageSocketHandler, "/ws/pages")
                .addInterceptors(new PageHandShakeInterceptor())
                .setAllowedOrigins("*");
    }
}
