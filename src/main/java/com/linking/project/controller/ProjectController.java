package com.linking.project.controller;

import com.linking.global.common.ResponseHandler;
import com.linking.sse.group.handler.GroupSseHandler;
import com.linking.participant.service.ParticipantService;
import com.linking.project.dto.ProjectContainsPartsRes;
import com.linking.project.dto.ProjectUpdateReq;
import com.linking.project.service.ProjectService;
import com.linking.project.dto.ProjectCreateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final GroupSseHandler groupSseHandler;
    private final ProjectService projectService;
    private final ParticipantService participantService;

    @PostMapping
    public ResponseEntity<ProjectContainsPartsRes> postProject(@RequestBody @Valid ProjectCreateReq projectCreateReq) {
        return projectService.createProject(projectCreateReq)
                .map(ResponseHandler::generateCreatedResponse)
                .orElseGet(ResponseHandler::generateInternalServerErrorResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectContainsPartsRes> getProject(@PathVariable Long id) {
        return projectService.getProjectsContainingParts(id)
                .map(p -> ResponseHandler.generateOkResponse(p))
                .orElseGet(ResponseHandler::generateInternalServerErrorResponse);
    }

    @GetMapping("/list/owner/{id}")
    public ResponseEntity<List<ProjectContainsPartsRes>> getProjectListByOwner(@PathVariable Long id){
        List<ProjectContainsPartsRes> projectList = projectService.getProjectsByOwnerId(id);
        if(projectList.isEmpty())
            return ResponseHandler.generateInternalServerErrorResponse();
        return ResponseHandler.generateOkResponse(projectList);
    }

    @GetMapping("/list/part/{id}")
    public ResponseEntity<List<ProjectContainsPartsRes>> getProjectListByPart(@PathVariable Long id){
        List<ProjectContainsPartsRes> projectList = projectService.getProjectsByUserId(id);
        if(projectList.isEmpty())
            return ResponseHandler.generateInternalServerErrorResponse();
        return ResponseHandler.generateOkResponse(projectList);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<ProjectContainsPartsRes> putProject(@RequestBody @Valid ProjectUpdateReq projectUpdateReq){
        return projectService.updateProject(projectUpdateReq, participantService.updateParticipantList(projectUpdateReq))
                .map(p -> ResponseHandler.generateOkResponse(p))
                .orElseGet(ResponseHandler::generateInternalServerErrorResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        groupSseHandler.removeEmittersByProject(id);
        return ResponseHandler.generateNoContentResponse();
    }

}
