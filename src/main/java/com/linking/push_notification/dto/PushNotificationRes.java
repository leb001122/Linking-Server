package com.linking.push_notification.dto;

import com.linking.push_notification.domain.NoticeType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushNotificationRes {

    @NotNull
    private Long projectId;
    @NotNull
    private String body;  // 페이지 / 할일 title
    @NotNull
    private String info;  // projectName + sender + createdDate
    @NotNull
    private int priority;   // 0 / 1
    @NotNull
    private NoticeType noticeType; // TODO / PAGE
    @NotNull
    private boolean isChecked;
    private Long targetId; // pageId
    private Long assistantId; // groupId
}
