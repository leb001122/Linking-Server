package com.linking.push_notification.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FcmReq {

    private String title;
    private String body;
    private String firebaseToken;
    private String link;
    private Map<String, String> data;
}
