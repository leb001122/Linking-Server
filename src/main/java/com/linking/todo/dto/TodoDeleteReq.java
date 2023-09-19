package com.linking.todo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDeleteReq {

    @NotNull
    private int emitterId;

    @NotNull
    private Long todoId;

}
