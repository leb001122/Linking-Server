package com.linking.socket.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushMessageRes<T> {

    private String resType;
    private T data;

    @Builder
    public PushMessageRes(String resType, T data) {
        this.resType = resType;
        this.data = data;
    }
}
