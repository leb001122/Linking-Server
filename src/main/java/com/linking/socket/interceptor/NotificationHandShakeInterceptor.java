package com.linking.socket.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class NotificationHandShakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        HttpServletRequest servletRequest = serverRequest.getServletRequest();

        try {
            attributes.put("userId", Long.valueOf(servletRequest.getParameter("userId")));

        } catch (RuntimeException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
