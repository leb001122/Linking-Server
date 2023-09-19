package com.linking.socket.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
public class PageHandShakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        HttpServletRequest servletRequest = serverRequest.getServletRequest();

        try {
            attributes.put("projectId", Long.valueOf(servletRequest.getParameter("projectId")));
            attributes.put("pageId", Long.valueOf(servletRequest.getParameter("pageId")));
            attributes.put("userId", Long.valueOf(servletRequest.getParameter("userId")));

        } catch (RuntimeException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
