package com.linking.push_settings.controller;

import com.linking.global.common.ResponseHandler;
import com.linking.push_settings.dto.PushSettingsUpdateReq;
import com.linking.push_settings.service.PushSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/push-settings")
public class PushSettingsController {

    private final PushSettingsService pushSettingsService;

    @PutMapping("/app")
    public ResponseEntity putAppPushSettings(
            @RequestBody @Valid PushSettingsUpdateReq req
    ) {
        return ResponseHandler.generateOkResponse(pushSettingsService.updateAppSettings(req));
    }

    @PutMapping("/web")
    public ResponseEntity putWebPushSettings(
            @RequestBody @Valid PushSettingsUpdateReq req
    ) {
        return ResponseHandler.generateOkResponse(pushSettingsService.updateWebSettings(req));
    }

    @GetMapping("/app/{userId}")
    public ResponseEntity getAppPushSettings(
            @PathVariable Long userId
    ) {
        return ResponseHandler.generateOkResponse(pushSettingsService.findAppPushSettingByUser(userId));
    }

    @GetMapping("/web/{userId}")
    public ResponseEntity getWebPushSettings(
            @PathVariable Long userId
    ) {
        return ResponseHandler.generateOkResponse(pushSettingsService.findWebPushSettingByUser(userId));
    }
}
