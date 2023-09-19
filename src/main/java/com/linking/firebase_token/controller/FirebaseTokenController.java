package com.linking.firebase_token.controller;

import com.linking.firebase_token.dto.TokenReq;
import com.linking.firebase_token.service.FirebaseTokenService;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/fcm-token")
public class FirebaseTokenController {

    private final FirebaseTokenService firebaseTokenService;

    // TODO app token 요청
    @PutMapping("/app")
    public ResponseEntity putAppFcmToken(
            @RequestBody @Valid TokenReq req
    ) {
        return ResponseHandler.generateOkResponse(firebaseTokenService.updateAppToken(req));
    }

    // TODO web token 요청
    @PutMapping("/web")
    public ResponseEntity putWebFcmToken(
            @RequestBody @Valid TokenReq req
    ) {
        log.info("put web fcm token");
        return ResponseHandler.generateOkResponse(firebaseTokenService.updateWebToken(req));
    }
}
