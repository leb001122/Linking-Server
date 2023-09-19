package com.linking.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoSseDeleteData {

    private Long todoId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long parentId;

}
