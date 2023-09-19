package com.linking.block.dto;

import com.linking.annotation.dto.AnnotationRes;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlockDetailRes {

    private Long blockId;
    private Long pageId;
    private String title;
    private String content;
    private List<AnnotationRes> annotationResList;

}
