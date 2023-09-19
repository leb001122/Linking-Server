package com.linking.page.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageTitleReq {

    private Long projectId;
    private Long groupId;
    private Long pageId;
    private String title;
}
