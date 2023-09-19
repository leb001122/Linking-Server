package com.linking.assign.service;

import com.linking.assign.domain.Assign;
import com.linking.assign.domain.Status;
import com.linking.assign.dto.*;
import com.linking.assign.persistence.AssignMapper;
import com.linking.assign.persistence.AssignRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.domain.Project;
import com.linking.todo.domain.Todo;
import com.linking.todo.dto.TodoUpdateReq;
import com.linking.todo.persistence.TodoRepository;
import com.linking.todo.controller.TodoSseEventHandler;
import com.linking.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignService {

    private final TodoSseEventHandler todoSseEventHandler;

    private final AssignRepository assignRepository;
    private final AssignMapper assignMapper;

    private final ParticipantRepository participantRepository;
    private final TodoRepository todoRepository;
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<AssignRatioRes> getAssignCompletionRate(Long id) {
        List<Participant> participantList = participantRepository.findByProject(new Project(id));
        List<AssignCountRes> countList =
                assignRepository.findCountByParticipantAndStatus(Status.COMPLETE, participantList);
        return assignMapper.toRatioDto(countList);
    }

    public Optional<AssignRes> updateAssignStatus(AssignStatusUpdateReq assignStatusUpdateReq){
        Optional<Assign> possibleAssign = assignRepository.findById(assignStatusUpdateReq.getAssignId());
        if(possibleAssign.isPresent()) {
            Assign.AssignBuilder assignBuilder = Assign.builder();
            Assign assign = assignBuilder
                    .assignId(assignStatusUpdateReq.getAssignId())
                    .todo(possibleAssign.get().getTodo())
                    .participant(possibleAssign.get().getParticipant())
                    .status(Status.valueOf(assignStatusUpdateReq.getStatus())).build();

            if(assign.getTodo().isParent())
                todoSseEventHandler.updateParentStatus(
                        assignStatusUpdateReq.getEmitterId(),
                        assign.getTodo().getProject().getProjectId(),
                        assignMapper.toSseStatusUpdateData(assign));
            else
                todoSseEventHandler.updateChildStatus(
                        assignStatusUpdateReq.getEmitterId(),
                        assign.getTodo().getProject().getProjectId(),
                        assignMapper.toSseStatusUpdateData(assign));
            return Optional.ofNullable(assignMapper.toResDto(assignRepository.save(assignBuilder.build())));
        }
        return Optional.empty();
    }

    public List<Long> updateAssignList(TodoUpdateReq todoUpdateReq){
        List<Long> resAssignList = new ArrayList<>();
        List<Assign> curAssignList = assignRepository.findByTodo(new Todo(todoUpdateReq.getTodoId()));
        List<Long> curPartIdList = curAssignList.stream()
                .map(a -> a.getParticipant().getParticipantId()).collect(Collectors.toList());
        List<Long> reqPartIdList = new ArrayList<>();
        for(Long id : todoUpdateReq.getAssignList())
            reqPartIdList.add(
                    participantRepository.findByUserAndProjectId(id, todoUpdateReq.getProjectId())
                            .orElseThrow(NoSuchElementException::new).getParticipantId());

        Assign.AssignBuilder assignBuilder = Assign.builder();
        for(int i = 0, skippedIndex; i < reqPartIdList.size(); i++){
            skippedIndex = curPartIdList.indexOf(reqPartIdList.get(i));
            if(skippedIndex == -1 || curAssignList.isEmpty()){
                Participant participant = participantRepository.findById(reqPartIdList.get(i))
                        .orElseThrow(NoSuchElementException::new);
                assignBuilder
                        .todo(new Todo(todoUpdateReq.getTodoId()))
                        .participant(participant)
                        .status(Status.BEFORE_START);
                resAssignList.add(
                        assignRepository.save(assignBuilder.build()).getAssignId());
            }
            else{
                resAssignList.add(curAssignList.get(skippedIndex).getAssignId());
                curAssignList.remove(skippedIndex);
            }
        }
        assignRepository.deleteAll(curAssignList);
        return resAssignList;
    }

    public void deleteAssign(AssignDeleteReq assignDeleteReq){
        Participant participant = participantRepository
                .findByUserAndProject(new User(assignDeleteReq.getUserId()), new Project(assignDeleteReq.getProjectId()))
                .orElseThrow(NoSuchElementException::new);
        Assign assign = assignRepository.findByTodoAndParticipant(new Todo(assignDeleteReq.getTodoId()), participant);

        if(assign.getTodo().getAssignList().size() == 1)
            todoRepository.delete(assign.getTodo());
        else
            assignRepository.delete(assign);
    }

    public void setAssignStatus(){
        Assign.AssignBuilder assignBuilder = Assign.builder();
        List<Assign> assignList = assignRepository.findByDateAndStatus(LocalDate.now(), Status.BEFORE_START);
        for(Assign assign : assignList)
            assignRepository.save(assignBuilder
                    .assignId(assign.getAssignId())
                    .todo(assign.getTodo())
                    .participant(assign.getParticipant())
                    .status(Status.INCOMPLETE).build());

        assignList = assignRepository.findByDateAndStatus(LocalDate.now(), Status.IN_PROGRESS);
        for(Assign assign : assignList)
            assignRepository.save(assignBuilder
                    .assignId(assign.getAssignId())
                    .todo(assign.getTodo())
                    .participant(assign.getParticipant())
                    .status(Status.INCOMPLETE_PROGRESS).build());
    }

}
