package com.linking.project.service;

import com.linking.chatroom.service.ChatRoomService;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.dto.ProjectContainsPartsRes;
import com.linking.project.dto.ProjectUpdateReq;
import com.linking.project.persistence.ProjectRepository;
import com.linking.project.dto.ProjectCreateReq;
import com.linking.project.persistence.ProjectMapper;
import com.linking.project.domain.Project;

import com.linking.user.domain.User;
import com.linking.user.dto.UserDetailedRes;
import com.linking.user.persistence.UserMapper;
import com.linking.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ParticipantRepository participantRepository;

    private final ChatRoomService chatRoomService;

    public Optional<ProjectContainsPartsRes> createProject(ProjectCreateReq projectCreateReq) throws DataIntegrityViolationException {
        Project project = projectRepository.save(projectMapper.toEntity(projectCreateReq));

        List<User> userList = userRepository.findAllById(projectCreateReq.getPartList());
        List<Participant> participantList = new ArrayList<>();
        for(User user : userList) {
            Participant.ParticipantBuilder participantBuilder = Participant.builder();
            participantList.add(participantRepository.save(
                    participantBuilder
                            .project(project)
                            .user(user)
                            .userName(user.getFullName()).build()));
        }
        project.setParticipantList(participantList);

        chatRoomService.createChatRoom(project);
        return Optional.ofNullable(projectMapper.toDto(project, userMapper.toDto(userList)));
    }

    public Optional<ProjectContainsPartsRes> getProjectsContainingParts(Long projectId) throws NoSuchElementException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NoSuchElementException::new);
        List<UserDetailedRes> partList = userMapper.toDto(project.getParticipantList().stream()
                        .map(Participant::getUser).collect(Collectors.toList()));

        return Optional.ofNullable(projectMapper.toDto(project, partList));
    }

    public List<ProjectContainsPartsRes> getProjectsByOwnerId(Long ownerId) throws NoSuchElementException{
        List<Project> projectList = projectRepository.findByOwner(ownerId);
        if(projectList.isEmpty())
            throw new NoSuchElementException();
        return projectMapper.toDto(projectList);
    }

    public List<ProjectContainsPartsRes> getProjectsByUserId(Long userId) throws NoSuchElementException {
        List<Project> projectList = participantRepository.findProjectsByUser(userId);
        if(projectList.isEmpty())
            throw new NoSuchElementException();

        List<ProjectContainsPartsRes> projectResList = new ArrayList<>();
        for(Project project : projectList){
            List<UserDetailedRes> partList = userMapper.toDto(project.getParticipantList().stream()
                    .map(Participant::getUser).collect(Collectors.toList()));
            projectResList.add(projectMapper.toDto(project, partList));
        }

        return projectResList;
    }

    public Optional<ProjectContainsPartsRes> updateProject(ProjectUpdateReq projectUpdateReq, List<Long> partIdList) throws NoSuchElementException{
        Project res =
                projectRepository.save(
                        projectMapper.toEntity(
                                projectUpdateReq, participantRepository.findAllById(partIdList)));
        List<UserDetailedRes> partList = userMapper.toDto(res.getParticipantList().stream()
                .map(Participant::getUser).collect(Collectors.toList()));

        return Optional.ofNullable(projectMapper.toDto(res, partList));
    }

    public void deleteProject(Long projectId) throws DataIntegrityViolationException, EmptyResultDataAccessException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NoSuchElementException::new);
        if(project.getParticipantList().size() > 1)
            throw new DataIntegrityViolationException("삭제할 수 없는 프로젝트");
        projectRepository.deleteById(projectId);
    }
}
