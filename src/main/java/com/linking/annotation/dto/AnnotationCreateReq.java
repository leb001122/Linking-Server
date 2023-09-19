package com.linking.annotation.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AnnotationCreateReq {

    @NotNull
    @Size(min = 1, max = 255)
    private String content;

    @NotNull
    private Long projectId;

    @NotNull
    private Long blockId;
}
