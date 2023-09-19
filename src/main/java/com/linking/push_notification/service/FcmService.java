package com.linking.push_notification.service;

import com.google.firebase.messaging.*;
import com.linking.push_notification.dto.FcmReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    @Async("eventCallExecutor")
    public void sendMessageToFcmServer(FcmReq req) {

        Notification notification = Notification.builder()
                .setTitle(req.getTitle()) // title
                .setBody(req.getBody())   // body
                .build();

        WebpushConfig webpushConfig = WebpushConfig.builder()
                .putHeader("Urgency", "normal")
                .setFcmOptions(WebpushFcmOptions.builder().setLink(req.getLink()).build())  // web link
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setBadge(0)
                        .putCustomData("link", req.getLink())
                        .build())
                .putHeader("apns-priority", "5")
                .build();

        Message message = Message.builder()
                .setToken(req.getFirebaseToken()) // token
                .setNotification(notification)
                .setWebpushConfig(webpushConfig)
                .setApnsConfig(apnsConfig)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully send FcmMessage: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Error sending FcmMessage\n" +
                    "getMessage() -> {}\n" +
                    "getDetailedErrorCode() -> {}\n" +
                    "getHttpResponse() -> {}", e.getMessage(), e.getMessagingErrorCode(), e.getHttpResponse());
        }
    }
}

