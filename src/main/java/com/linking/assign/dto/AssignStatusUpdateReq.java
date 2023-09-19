package com.linking.assign.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignStatusUpdateReq {

    private int emitterId;
    private Long assignId;
    private String status;

}
