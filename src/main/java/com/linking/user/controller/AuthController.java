package com.linking.user.controller;

import com.linking.firebase_token.service.FirebaseTokenService;
import com.linking.user.dto.UserDetailedRes;
import com.linking.user.dto.UserEmailVerifyReq;
import com.linking.user.dto.UserSignUpReq;
import com.linking.user.service.UserService;
import com.linking.global.common.ResponseHandler;
import com.linking.push_settings.service.PushSettingsService;
import com.linking.user.dto.UserSignInReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final UserService userService;
    private final FirebaseTokenService firebaseTokenService;
    private final PushSettingsService pushSettingsService;

    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUpDefault(@RequestBody @Valid UserSignUpReq userSignUpReq){
//        return userService.addUser(userSignUpReq)
//                .map(u -> ResponseHandler.generateResponse(ResponseHandler.MSG_201, HttpStatus.CREATED, u))
//                .orElseGet(ResponseHandler::generateInternalServerErrorResponse);

        UserDetailedRes res = userService.addUser(userSignUpReq);
        firebaseTokenService.createFirebaseToken(res.getUserId());
        pushSettingsService.createPushSettings(res.getUserId());
        return ResponseHandler.generateCreatedResponse(res);
    }

    @PostMapping("/verify/email")
    public ResponseEntity<Object> verifyEmail(@RequestBody @Valid UserEmailVerifyReq emailReq) {
        if(userService.isUniqueEmail(emailReq))
            return ResponseHandler.generateResponse("이미 존재하는 이메일", HttpStatus.OK, false);
        return ResponseHandler.generateResponse("고유한 이메일", HttpStatus.OK, true);
    }

    // TODO: login security needs to be upgraded
//    @PostMapping("/sign-in")
//    public ResponseEntity<Object> signIn(@RequestBody @Valid UserSignInReq userSignInReq){
//        return userService.getUserWithEmailAndPw(userSignInReq)
//                .map(u -> ResponseHandler.generateResponse(ResponseHandler.MSG_200, HttpStatus.OK, u))
//                .orElseGet(ResponseHandler::generateInternalServerErrorResponse);
//    }

    @PostMapping("/sign-in")
    public ResponseEntity signIn(
            @RequestBody @Valid UserSignInReq req
    ) {
        UserDetailedRes res = userService.getUserWithEmailAndPw(req);
        return ResponseHandler.generateOkResponse(res);
    }
}
