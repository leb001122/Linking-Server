package com.linking.todo.service;

import com.linking.assign.domain.Assign;
import com.linking.assign.domain.Status;
import com.linking.assign.persistence.AssignRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.domain.Project;
import com.linking.user.domain.User;
import com.linking.todo.domain.Todo;
import com.linking.todo.dto.*;
import com.linking.todo.persistence.TodoMapper;
import com.linking.todo.persistence.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    private final ParticipantRepository participantRepository;
    private final AssignRepository assignRepository;

    public TodoSingleRes createTodo(TodoCreateReq todoCreateReq){
        Todo todo = todoRepository.save(todoMapper.toEntity(todoCreateReq));

        List<Participant> participantList =
                participantRepository.findByProjectAndUser(
                        new Project(todoCreateReq.getProjectId()), todoCreateReq.getAssignList());
        Assign.AssignBuilder assignBuilder = Assign.builder();

        List<Assign> assignList = new ArrayList<>();
        for(Participant participant : participantList)
            assignList.add(assignRepository.save(
                    assignBuilder
                            .todo(todo)
                            .participant(participant)
                            .status(Status.BEFORE_START).build()));
        todo.setAssignList(assignList);
        return todoMapper.toResDto(todo);
    }

    public Optional<TodoSingleRes> getTodo(Long id){
        return todoRepository.findByTodoId(id)
                .map(todoMapper::toResDto);
    }

    public List<TodoSimpleRes> getTodayUserUrgentTodos(Long id){
        List<Assign> assignList = assignRepository.findByParticipantAndStatusAndDate(id, LocalDate.now());
        assignList.addAll(assignRepository.findByParticipantAndDate(id, LocalDate.now()));
        return todoMapper.toSimpleDto(assignList);
    }

    public List<ParentTodoRes> getTodayProjectUrgentTodos(Long id){
        List<Todo> todoList = assignRepository.findByProjectAndStatusAndDate(id, LocalDate.now())
                .stream().map(Assign::getTodo).collect(Collectors.toList());
        todoList.addAll(todoRepository.findByProjectAndMonth(id, LocalDate.now()));
        return todoMapper.toParentDto(excludeUnnecessaryTodos(todoList, LocalDate.now(), false));
    }

    public List<ParentTodoRes> getDailyProjectTodos(Long id, int year, int month, int day){
        List<Todo> todoList = assignRepository.findByProjectAndStatusAndDate(id, LocalDate.of(year, month, day))
                .stream().map(Assign::getTodo).collect(Collectors.toList());
        List<Todo> tdl = todoRepository.findByProjectAndDateContains(id, LocalDate.of(year, month, day));
        todoList.addAll(tdl);
        return todoMapper.toParentDto(excludeUnnecessaryTodos(todoList, LocalDate.of(year, month, day), false));
    }

    public List<ParentTodoRes> getMonthlyProjectTodos(Long id, int year, int month){
        List<Todo> todoList = assignRepository.findByProjectAndStatusAndDate(id, LocalDate.of(year, month, 1))
                .stream().map(Assign::getTodo).collect(Collectors.toList());
        todoList.addAll(todoRepository.findByProjectAndMonthContains(new Project(id), LocalDate.of(year, month, 1)));
        return todoMapper.toParentDto(excludeUnnecessaryTodos(todoList, LocalDate.of(year, month, 1), true));
    }

    // TODO: To be deleted
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<ParentTodoRes> getDailyUserTodos(Long id, int year, int month, int day){
        List<Participant> participantList = participantRepository.findByUser(new User(id));
        List<Todo> todoList =
                assignRepository.findByParticipantAndDateContains(participantList, LocalDate.of(year, month, day))
                        .stream().map(Assign::getTodo).collect(Collectors.toList());
        return todoMapper.toParentDto(todoList);
    }

    public TodoSingleRes updateTodo(TodoUpdateReq todoUpdateReq){
        List<Assign> assignList = todoRepository.findById(
                todoUpdateReq.getTodoId()).orElseThrow(NoSuchElementException::new).getAssignList();
        Todo todo = todoRepository.save(
                todoMapper.toEntity(todoUpdateReq,
                        assignList.stream().map(Assign::getAssignId).collect(Collectors.toList())));
        return todoMapper.toResDto(todo);
    }

    public TodoSingleRes updateTodo(TodoUpdateReq todoUpdateReq, List<Long> assignIdList){
        Todo todo = todoRepository.save(todoMapper.toEntity(todoUpdateReq, assignIdList));
        return todoMapper.toResDto(todo);
    }

    public Todo deleteTodo(TodoDeleteReq todoDeleteReq){
        Todo todo = todoRepository.findById(todoDeleteReq.getTodoId()).orElseThrow(NoSuchElementException::new);
        todoRepository.deleteById(todoDeleteReq.getTodoId());
        return todo;
    }

    private List<Todo> excludeUnnecessaryTodos(List<Todo> todoList, LocalDate currentDate, boolean isMonthly){
        for(Todo todo : todoList){
            if(todo.isParent()){
                if(isMonthly)
                    todo.setChildTodoList(excludeUnnecessaryTodos(todo.getChildTodoList(), currentDate, 7));
                else
                    todo.setChildTodoList(excludeUnnecessaryTodos(todo.getChildTodoList(), currentDate, 10));
            }
        }
        return todoList;
    }

    private List<Todo> excludeUnnecessaryTodos(List<Todo> todoList, LocalDate inDate, int lastIndex){
        List<Todo> retTodos = new ArrayList<>();
        int startDate, dueDate, currentDate;
        currentDate = Integer.parseInt(inDate.toString().substring(0, lastIndex).replace("-", ""));
        for(Todo todo : todoList){
            startDate = Integer.parseInt(todo.getStartDate().toString().substring(0, lastIndex).replace("-", ""));
            dueDate = Integer.parseInt(todo.getDueDate().toString().substring(0, lastIndex).replace("-", ""));
            if(startDate <= currentDate && currentDate <= dueDate)
                retTodos.add(todo);

        }
        return retTodos;
    }

}
