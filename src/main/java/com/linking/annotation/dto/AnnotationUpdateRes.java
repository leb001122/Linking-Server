package com.linking.annotation.dto;

import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AnnotationUpdateRes {

    private Long annotationId;
    private Long blockId;
    private String content;
    private String lastModified;
}
