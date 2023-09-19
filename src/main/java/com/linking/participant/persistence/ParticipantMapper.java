package com.linking.participant.persistence;

import com.linking.participant.domain.Participant;
import com.linking.participant.dto.ParticipantIdReq;
import com.linking.participant.dto.ParticipantRes;
import com.linking.participant.dto.ParticipantSimplifiedRes;
import com.linking.project.domain.Project;
import com.linking.user.domain.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    default ParticipantRes toDto(Participant participant) {
        if(participant == null)
            return null;

        ParticipantRes.ParticipantResBuilder partResBuilder = ParticipantRes.builder();
        partResBuilder
                .participantId(participant.getParticipantId())
                .userId(participant.getUser().getUserId())
                .projectId(participant.getProject().getProjectId());

        return partResBuilder.build();
    }

    default List<ParticipantRes> toDto(List<Participant> partList) {
        if(partList.isEmpty())
            return null;
        return partList.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<ParticipantSimplifiedRes> toSimpleDto(List<Participant> participantList){
        if(participantList == null)
            return null;
        return participantList.stream().map(this::toSimpleDto).collect(Collectors.toList());
    }

    default ParticipantSimplifiedRes toSimpleDto(Participant participant){
        if(participant == null)
            return null;
        ParticipantSimplifiedRes.ParticipantSimplifiedResBuilder participantSimplifiedResBuilder =
                ParticipantSimplifiedRes.builder();
        return participantSimplifiedResBuilder
                .userId(participant.getUser().getUserId())
                .userName(participant.getUserName()).build();
    }

    default Participant toEntity(ParticipantIdReq participantIdReq){
        if(participantIdReq == null)
            return null;

        Participant.ParticipantBuilder partBuilder = Participant.builder();
        partBuilder
                .user(new User(participantIdReq.getUserId()))
                .project(new Project(participantIdReq.getProjectId()));

        return partBuilder.build();
    }

}
