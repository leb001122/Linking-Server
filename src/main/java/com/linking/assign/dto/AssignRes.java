package com.linking.assign.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignRes {

    private Long assignId;
    private Long userId;
    private String userName;
    private String status;

}
