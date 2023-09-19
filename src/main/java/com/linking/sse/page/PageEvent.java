package com.linking.sse.page;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageEvent<T> {

    private String eventName;
    private Long pageId;
    private Long userId;
    private T data;

    @Builder
    public PageEvent(String eventName, Long pageId, Long userId, T data) {
        this.eventName = eventName;
        this.pageId = pageId;
        this.userId = userId;
        this.data = data;
    }
}
