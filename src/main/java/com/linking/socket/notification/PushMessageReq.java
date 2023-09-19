package com.linking.socket.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushMessageReq {

    private Boolean isChecking;

    public PushMessageReq(Boolean isChecking) {
        this.isChecking = isChecking;
    }
}
