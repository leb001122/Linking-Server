package com.linking.annotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationUpdateReq {

    @NotNull
    private Long annotationId;

    @NotNull
    @Size(min = 1, max = 255)
    private String content;
}
