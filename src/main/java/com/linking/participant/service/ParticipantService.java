package com.linking.participant.service;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chatroom.repository.ChatRoomRepository;
import com.linking.chatroom_badge.domain.ChatRoomBadge;
import com.linking.chatroom_badge.persistence.ChatRoomBadgeRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.dto.ParticipantIdReq;
import com.linking.participant.dto.ParticipantRes;
import com.linking.participant.dto.ParticipantSimplifiedRes;
import com.linking.page_check.service.PageCheckService;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.participant.dto.ParticipantDeleteReq;
import com.linking.participant.persistence.ParticipantMapper;
import com.linking.project.domain.Project;
import com.linking.project.dto.ProjectUpdateReq;
import com.linking.project.persistence.ProjectRepository;
import com.linking.user.domain.User;
import com.linking.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final PageCheckService pageCheckService;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomBadgeRepository chatRoomBadgeRepository;

    public Optional<ParticipantRes> createParticipant(ParticipantIdReq participantIdReq)
            throws DataIntegrityViolationException {
        Optional<Participant> possibleParticipant = participantRepository
                .findByUserAndProject(new User(participantIdReq.getUserId()), new Project(participantIdReq.getProjectId()));
        if(possibleParticipant.isPresent())
            throw new DuplicateKeyException("Already in project");

        Participant participant = participantRepository.save(participantMapper.toEntity(participantIdReq));


        return Optional.of(participantMapper.toDto(participant));
    }

    public Optional<ParticipantRes> getParticipant(Long participantId)
            throws NoSuchElementException {
        return Optional.ofNullable(participantRepository.findById(participantId)
                .map(participantMapper::toDto)
                .orElseThrow(NoSuchElementException::new));
    }

    public List<ParticipantSimplifiedRes> getParticipantsByProjectId(Long projectId)
            throws NoSuchElementException {
        List<Participant> participantList = participantRepository.findByProject(new Project(projectId));
        if (participantList.isEmpty())
            throw new NoSuchElementException();
        return participantMapper.toSimpleDto(participantList);
    }

    public List<Long> updateParticipantList(ProjectUpdateReq projectUpdateReq)
        throws DataIntegrityViolationException {
        List<Long> resPartIdList = new ArrayList<>();
        if(!projectUpdateReq.getIsPartListChanged()){
            for(Long id : projectUpdateReq.getPartList())
                resPartIdList.add(participantRepository.findByUserAndProjectId(id, projectUpdateReq.getProjectId())
                        .orElseThrow(NoSuchElementException::new).getParticipantId());
            return resPartIdList;
        }

        Project project = projectRepository.findById(projectUpdateReq.getProjectId())
                .orElseThrow(NoSuchElementException::new);
        List<Participant> curPartList =
                participantRepository.findByProject(project);
        List<Long> curUserIdList = curPartList.stream()
                .map(p->p.getUser().getUserId()).collect(Collectors.toList());
        List<Long> reqPartUserList = projectUpdateReq.getPartList();


        if(!project.getOwner().getUserId().equals(reqPartUserList.get(0)))
            throw new DataIntegrityViolationException("삭제할 수 없는 팀원");

        List<Participant> newParticipantList = new ArrayList<>();
        Participant.ParticipantBuilder participantBuilder = Participant.builder();
        for(int i = 0, index; i < reqPartUserList.size(); i++){
            index = curUserIdList.indexOf(reqPartUserList.get(i));
            if(index == -1 || curPartList.isEmpty()){
                User user = userRepository.findById(reqPartUserList.get(i))
                        .orElseThrow(NoSuchElementException::new);
                participantBuilder
                        .project(new Project(projectUpdateReq.getProjectId()))
                        .user(user)
                        .userName(user.getFullName());
                Participant participant = participantRepository.save(participantBuilder.build());
                newParticipantList.add(participant);
                resPartIdList.add(participant.getParticipantId());
            }
            else{
                resPartIdList.add(curPartList.get(index).getParticipantId());
                curPartList.remove(index);
                curUserIdList.remove(index);
            }
        }
        participantRepository.deleteAll(curPartList);

        if(!newParticipantList.isEmpty()){
            ChatRoom chatRoom = chatRoomRepository.findChatRoomByProject(project)
                    .orElseThrow(NoSuchElementException::new);
            ChatRoomBadge.ChatRoomBadgeBuilder chatRoomBadgeBuilder = ChatRoomBadge.builder();
            List<ChatRoomBadge> chatRoomBadgeList = new ArrayList<>();
            for(int i = 0; i < newParticipantList.size(); i++)
                chatRoomBadgeList.add(
                        chatRoomBadgeBuilder
                                .participant(newParticipantList.get(i))
                                .chatRoom(chatRoom)
                                .unreadCount(0)
                                .build()
                );
            chatRoomBadgeRepository.saveAll(chatRoomBadgeList);
        }

        Long ownerId = project.getOwner().getUserId();
        for(int i = 0; i < resPartIdList.size(); i++) {
            if (resPartIdList.get(i).equals(ownerId)) {
                Collections.swap(resPartIdList, 0, i);
                break;
            }
        }

        // todo newParticipant 의 pageCheck 생성
        if (!newParticipantList.isEmpty())
            pageCheckService.createPageCheck(project.getProjectId(), newParticipantList);

        return resPartIdList;
    }

    public void deleteParticipant(ParticipantDeleteReq participantDeleteReq)
            throws NoSuchElementException, DataIntegrityViolationException {
        List<Participant> participantList = setParticipantList(participantDeleteReq.getPartIdList());
        if(participantList.isEmpty())
            throw new NoSuchElementException();
        if(containsOwner(participantList)) {
            throw new DataIntegrityViolationException("삭제할 수 없는 팀원");
        }
        participantRepository.deleteAll(participantList);
    }

    private List<Participant> setParticipantList(List<ParticipantIdReq> participantIdReqList){
        List<Participant> participantList = new ArrayList<>();
        for(ParticipantIdReq p : participantIdReqList){
            participantList.add(
                    participantRepository.findByUserAndProject(new User(p.getUserId()), new Project(p.getProjectId()))
                            .orElseThrow(NoSuchElementException::new));
        }
        return participantList;
    }

    private boolean containsOwner(List<Participant> participantList){
        for(Participant p: participantList){
            if(p.getProject().getOwner().getUserId().equals(p.getUser().getUserId()))
                return true;
        }
        return false;
    }
}
