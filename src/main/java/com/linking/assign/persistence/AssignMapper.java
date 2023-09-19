package com.linking.assign.persistence;

import com.linking.assign.domain.Assign;
import com.linking.assign.dto.AssignCountRes;
import com.linking.assign.dto.AssignRatioRes;
import com.linking.assign.dto.AssignRes;
import com.linking.assign.dto.AssignSseUpdateData;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface AssignMapper {

    default AssignRatioRes toRatioDto(AssignCountRes countReq) {
        if (countReq == null)
            return null;

        AssignRatioRes.AssignRatioResBuilder assignRatioResBuilder = AssignRatioRes.builder();
        assignRatioResBuilder
                .userName(countReq.getParticipant().getUserName())
                .totalAssign(countReq.getCount())
                .completeAssign(countReq.getCompleteCount())
                .completionRatio(0.0);

        if(countReq.getCount() > 0)
            assignRatioResBuilder.completionRatio((double) countReq.getCompleteCount() / countReq.getCount() * 100);
        return assignRatioResBuilder.build();
    }

    default List<AssignRatioRes> toRatioDto(List<AssignCountRes> countList) {
        if (countList == null)
            return null;

        List<AssignRatioRes> assignRatioList = new ArrayList<>();
        for (AssignCountRes assignCountRes : countList)
            assignRatioList.add(toRatioDto(assignCountRes));

        return assignRatioList;
    }

    default AssignRes toResDto(Assign assign) {
        if (assign == null)
            return null;

        AssignRes.AssignResBuilder assignResBuilder = AssignRes.builder();
        return assignResBuilder
                .assignId(assign.getAssignId())
                .userId(assign.getParticipant().getUser().getUserId())
                .userName(assign.getParticipant().getUserName())
                .status(String.valueOf(assign.getStatus())).build();
    }

    List<AssignRes> toResDto(List<Assign> assignList);

    default AssignSseUpdateData toSseStatusUpdateData(Assign assign) {
        if (assign == null)
            return null;

        AssignSseUpdateData.AssignSseUpdateDataBuilder assignSseUpdateDataBuilder = AssignSseUpdateData.builder();
        assignSseUpdateDataBuilder
                .todoId(assign.getTodo().getTodoId())
                .assignId(assign.getAssignId())
                .status(String.valueOf(assign.getStatus()));

        if (!assign.getTodo().isParent())
            assignSseUpdateDataBuilder.parentId(assign.getTodo().getParentTodo().getTodoId());
        return assignSseUpdateDataBuilder.build();
    }

}
