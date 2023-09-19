package com.linking.push_notification.dto;

import com.linking.push_notification.domain.NoticeType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushNotificationReq {

    @NotNull
    private Long projectId;
    @NotNull
    private Long userId;
    @NotNull
    private String sender;
    @NotNull
    private int priority;
    @NotNull
    private NoticeType noticeType;
    private Long targetId; // 페이지인 경우에
    @NotNull
    private String body;
}
