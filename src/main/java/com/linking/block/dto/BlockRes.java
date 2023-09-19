package com.linking.block.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockRes {

    private Long blockId;
    private Long pageId;
    private String title;
    private String content;

    @Builder
    public BlockRes(Long blockId, Long pageId, String title, String content) {
        this.blockId = blockId;
        this.pageId = pageId;
        this.title = title;
        this.content = content;
    }
}
