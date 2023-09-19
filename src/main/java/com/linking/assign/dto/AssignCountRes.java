package com.linking.assign.dto;

import com.linking.participant.domain.Participant;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignCountRes {

    private Participant participant;
    private int count;
    private int completeCount;

    public AssignCountRes(Participant participant, Long count, Long completeCount){
        this.participant = participant;
        this.count = count.intValue();
        this.completeCount = completeCount.intValue();
    }

}
