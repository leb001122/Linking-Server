package com.linking.global.config;

import com.linking.socket.interceptor.NotificationHandShakeInterceptor;
import com.linking.socket.notification.handler.NotificationSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class NotificationWebSocketConfig implements WebSocketConfigurer {

    private final NotificationSocketHandler notificationSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(notificationSocketHandler, "/ws/push-notifications")
                .addInterceptors(new NotificationHandShakeInterceptor())
                .setAllowedOrigins("*");
    }
}
