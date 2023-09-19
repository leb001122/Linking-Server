package com.linking.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketSessionConfig {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        var container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(15 * 60 * 1000L);

//        container.setMaxTextMessageBufferSize(20000); // 테스트 메시지의 최대 크기 설정.
//        container.setMaxSessionIdleTimeout(1000L); // 웹 소켓 세션 유지 시간 설정.
//        container.setAsyncSendTimeout(1000L); // 응답을 보내기 위해 시도하는 시간 설정.

        return container;
    }
}
