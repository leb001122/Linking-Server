package com.linking.push_notification.controller;

import com.linking.push_notification.dto.PushNotificationReq;
import com.linking.push_notification.dto.PushNotificationRes;
import com.linking.push_notification.service.PushNotificationService;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/push-notifications")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @GetMapping("/{userId}")
    public ResponseEntity getAllNotifications(
            @PathVariable Long userId
    ) {

        List<PushNotificationRes> res = pushNotificationService.findAllPushNotificationsByUser(userId);
        return ResponseHandler.generateOkResponse(res);
    }

    @PostMapping
    public ResponseEntity postPushNotification(
            @RequestBody @Valid PushNotificationReq req
    ) {
        return ResponseHandler.generateOkResponse(pushNotificationService.sendPushNotification(req));
    }

    @DeleteMapping(path = "/{notificationId}")
    public ResponseEntity deleteNotification(
            @PathVariable Long notificationId
    ) {
        boolean res = pushNotificationService.deleteNotification(notificationId);
        return ResponseHandler.generateOkResponse(res);
    }
}
