package com.linking.socket.page;

import com.linking.page.domain.DiffStr;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageSocketMessageRes {

    private Long pageId;
    private Long blockId; // blockId or -1
    private Integer editorType; // 0, 1, 2
    private DiffStr diffStr;
}
