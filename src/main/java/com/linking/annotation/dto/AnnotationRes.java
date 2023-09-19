package com.linking.annotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AnnotationRes {

    private Long annotationId;
    private Long blockId;
    private String content;
    private String lastModified;
    private Long userId;
    private String userName;
}
