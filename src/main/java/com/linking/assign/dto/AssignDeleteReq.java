package com.linking.assign.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignDeleteReq {

    private int emitterId;
    private Long userId;
    private Long projectId;
    private Long todoId;

}
