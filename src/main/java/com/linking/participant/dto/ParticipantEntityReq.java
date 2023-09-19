package com.linking.participant.dto;

import com.linking.project.domain.Project;
import com.linking.user.domain.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantEntityReq {

    private User user;
    private Project project;

}
