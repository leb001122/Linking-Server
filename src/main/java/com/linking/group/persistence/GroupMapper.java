package com.linking.group.persistence;

import com.linking.group.domain.Group;
import com.linking.group.dto.GroupCreateReq;
import com.linking.group.dto.GroupRes;
import com.linking.page.dto.PageRes;
import com.linking.project.domain.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GroupMapper {

    default GroupRes toDto(Group source, List<PageRes> pageResList) {

        GroupRes builder = GroupRes.builder()
                .groupId(source.getId())
                .projectId(source.getProject().getProjectId())
                .name(source.getName())
                .pageResList(pageResList)
                .build();

        return builder;
    }

    default GroupRes toPostGroupResDto(Group source) {

        GroupRes builder = GroupRes.builder()
                .groupId(source.getId())
                .name(source.getName())
                .build();

        return builder;
    }

    default Group toEntity(GroupCreateReq source, Project project) {

        Group.GroupBuilder builder = Group.builder();
        builder
                .name(source.getName())
                .project(project)
                .pageList(new ArrayList<>());

        return builder.build();
    }
}
