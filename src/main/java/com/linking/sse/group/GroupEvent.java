package com.linking.sse.group;

import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEvent<T> {

    private String eventName;
    private Long projectId;
    private Long userId;
    private Set<Long> userIds;
    private T data;

    @Builder
    public GroupEvent(String eventName, Long projectId, Long userId, Set<Long> userIds, T data) {
        this.eventName = eventName;
        this.projectId = projectId;
        this.userId = userId;
        this.userIds = userIds;
        this.data = data;
    }
}
