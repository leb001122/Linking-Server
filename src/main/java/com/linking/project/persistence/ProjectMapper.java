package com.linking.project.persistence;

import com.linking.participant.domain.Participant;
import com.linking.project.domain.Project;
import com.linking.project.dto.ProjectContainsPartsRes;
import com.linking.project.dto.ProjectUpdateReq;
import com.linking.project.dto.ProjectCreateReq;
import com.linking.user.domain.User;
import com.linking.user.dto.UserDetailedRes;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    default ProjectContainsPartsRes toDto(Project project, List<UserDetailedRes> partList){
        if(project == null)
            return null;

        ProjectContainsPartsRes.ProjectContainsPartsResBuilder projResBuilder
                = ProjectContainsPartsRes.builder();
        projResBuilder
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .beginDate(project.getBeginDate())
                .dueDate(project.getDueDate())
                .ownerId(project.getOwner().getUserId())
                .partList(partList);

        return projResBuilder.build();
    }

    default List<ProjectContainsPartsRes> toDto(List<Project> projectList){
        List<ProjectContainsPartsRes> projectResList = new ArrayList<>();
        for (Project project : projectList) {
            ProjectContainsPartsRes.ProjectContainsPartsResBuilder projectResBuilder = ProjectContainsPartsRes.builder();
            projectResBuilder
                    .projectId(project.getProjectId())
                    .projectName(project.getProjectName())
                    .beginDate(project.getBeginDate())
                    .dueDate(project.getDueDate())
                    .ownerId(project.getOwner().getUserId())
                    .partList(new ArrayList<>());
                    // partList exists for FRONT-END TypeError prevention
            projectResList.add(projectResBuilder.build());
        }
        return projectResList;
    }

    default Project toEntity(ProjectCreateReq projectCreateReq) {
        if (projectCreateReq == null)
            return null;

        Project.ProjectBuilder projBuilder = Project.builder();
        projBuilder
                .projectName(projectCreateReq.getProjectName())
                .beginDate(projectCreateReq.getBeginDate())
                .dueDate(projectCreateReq.getDueDate())
                .owner(new User(projectCreateReq.getPartList().get(0)))
                .participantList(new ArrayList<>());

        return projBuilder.build();
    }

    default Project toEntity(ProjectUpdateReq projectUpdateReq, List<Participant> participantList) {
        if(projectUpdateReq == null)
            return null;

        Project.ProjectBuilder projBuilder = Project.builder();
        projBuilder
                .projectId(projectUpdateReq.getProjectId())
                .projectName(projectUpdateReq.getProjectName())
                .beginDate(projectUpdateReq.getBeginDate())
                .dueDate(projectUpdateReq.getDueDate())
                .owner(new User(projectUpdateReq.getPartList().get(0)))
                .participantList(participantList);

        return projBuilder.build();
    }

}
