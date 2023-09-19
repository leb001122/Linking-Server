package com.linking.firebase_token.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenReq {

    @NotNull
    private Long userId;
    @NotNull
    private String token;

    @Builder
    public TokenReq(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
