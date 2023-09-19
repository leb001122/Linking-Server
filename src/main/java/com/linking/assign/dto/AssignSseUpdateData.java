package com.linking.assign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignSseUpdateData {

    private Long todoId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long parentId;

    private Long assignId;
    private String status;

}
