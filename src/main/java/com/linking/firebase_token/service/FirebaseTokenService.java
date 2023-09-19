package com.linking.firebase_token.service;

import com.linking.firebase_token.domain.FirebaseToken;
import com.linking.firebase_token.dto.TokenReq;
import com.linking.firebase_token.persistence.FirebaseTokenRepository;
import com.linking.user.domain.User;
import com.linking.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseTokenService {

    private final FirebaseTokenRepository firebaseTokenRepository;
    private final UserRepository userRepository;

    public void createFirebaseToken(Long userId) {
        User user = userRepository.getReferenceById(userId);

        FirebaseToken firebaseToken = FirebaseToken.builder()
                .user(user)
                .build();
        firebaseTokenRepository.save(firebaseToken);
    }

    @Transactional
    public boolean updateAppToken(TokenReq req) {
        FirebaseToken firebaseToken = firebaseTokenRepository.findByUserId(req.getUserId())
                .orElseThrow(NoSuchElementException::new);

        firebaseToken.setAppToken(req.getToken());

        return true;
    }

    @Transactional
    public boolean updateWebToken(TokenReq req) {
        FirebaseToken firebaseToken = firebaseTokenRepository.findByUserId(req.getUserId())
                .orElseThrow(NoSuchElementException::new);
        log.info("web token = {}", req.getToken());

        firebaseToken.setWebToken(req.getToken());

        return true;
    }


    // TODO timestamp 주기적으로 업뎃
}
