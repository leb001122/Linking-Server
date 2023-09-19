package com.linking.participant.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantIdReq {

    @NotNull
    private Long userId;

    @NotNull
    private Long projectId;

}
