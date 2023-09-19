package com.linking.participant.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantDeleteReq {

    List<ParticipantIdReq> partIdList;

}
