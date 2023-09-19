package com.linking.socket.page;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextSendEvent {

    private String sessionId;
    private Long pageId;
    private PageSocketMessageRes pageSocketMessageRes;
}
