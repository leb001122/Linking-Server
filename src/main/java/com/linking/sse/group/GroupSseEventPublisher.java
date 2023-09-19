package com.linking.sse.group;

import com.linking.group.dto.GroupRes;
import com.linking.sse.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupSseEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishPostGroup(Long projectId, Long userId, GroupRes groupRes) {
        publisher.publishEvent(GroupEvent.builder()
                .eventName(EventType.POST_GROUP)
                .projectId(projectId)
                .userId(userId)
                .data(groupRes)
            .build());
    }

    public void publishPutGroupName(Long projectId, Long userId, GroupRes groupRes) {
        publisher.publishEvent(GroupEvent.builder()
                .eventName(EventType.PUT_GROUP_NAME)
                .projectId(projectId)
                .userId(userId)
                .data(groupRes)
                .build());
    }

    public void publishDeleteGroup(Long projectId, Long userId, GroupRes groupRes) {
        publisher.publishEvent(GroupEvent.builder()
                .eventName(EventType.DELETE_GROUP)
                .projectId(projectId)
                .userId(userId)
                .data(groupRes)
                .build());
    }
}
