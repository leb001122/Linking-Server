package com.linking.todo.persistence;

import com.linking.assign.domain.Assign;
import com.linking.assign.persistence.AssignMapper;
import com.linking.assign.persistence.AssignRepository;
import com.linking.project.domain.Project;
import com.linking.todo.domain.Todo;
import com.linking.todo.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TodoMapper {

    private final AssignRepository assignRepository;
    private final AssignMapper assignMapper;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a").withLocale(Locale.ENGLISH);

    public Todo toEntity(TodoCreateReq todoCreateReq){
        if(todoCreateReq == null)
            return null;

        Todo.TodoBuilder todoBuilder = Todo.builder();
        todoBuilder
                .project(new Project(todoCreateReq.getProjectId()))
                .isParent(todoCreateReq.getIsParent())
                .startDate(LocalDateTime.parse(todoCreateReq.getStartDate(), formatter))
                .dueDate(LocalDateTime.parse(todoCreateReq.getDueDate(), formatter))
                .content(todoCreateReq.getContent());

        if(!todoCreateReq.getIsParent())
            todoBuilder.parentTodo(new Todo(todoCreateReq.getParentId()));
        return todoBuilder.build();
    }

    public Todo toEntity(TodoUpdateReq todoUpdateReq, List<Long> assignIdList){
        if(todoUpdateReq == null)
            return null;

        List<Assign> assignList = assignRepository.findAllById(assignIdList);
        Todo.TodoBuilder todoBuilder = Todo.builder();
        todoBuilder
                .todoId(todoUpdateReq.getTodoId())
                .project(new Project(todoUpdateReq.getProjectId()))
                .isParent(todoUpdateReq.getIsParent())
                .startDate(LocalDateTime.parse(todoUpdateReq.getStartDate(), formatter))
                .dueDate(LocalDateTime.parse(todoUpdateReq.getDueDate(), formatter))
                .content(todoUpdateReq.getContent())
                .assignList(assignList);

        if(!todoUpdateReq.getIsParent())
            todoBuilder.parentTodo(new Todo(todoUpdateReq.getParentId()));
        return todoBuilder.build();
    }

    public TodoSingleRes toResDto(Todo todo) {
        if (todo == null)
            return null;

        TodoSingleRes.TodoSingleResBuilder todoResBuilder = TodoSingleRes.builder();
        todoResBuilder
                .todoId(todo.getTodoId())
                .isParent(todo.isParent())
                .parentId(-1L)
                .startDate(todo.getStartDate().format(formatter))
                .dueDate(todo.getDueDate().format(formatter))
                .content(todo.getContent())
                .assignList(assignMapper.toResDto(todo.getAssignList()));

        if (!todo.isParent())
            todoResBuilder.parentId(todo.getParentTodo().getTodoId());
        return todoResBuilder.build();
    }

    public List<TodoSingleRes> toResDto(List<Todo> todoList){
        if(todoList == null)
            return null;
        return todoList.stream().map(this::toResDto).collect(Collectors.toList());
    }

    public ParentTodoRes toParentDto(Todo todo){
        if(todo == null)
            return null;

        ParentTodoRes.ParentTodoResBuilder parentTodoResBuilder = ParentTodoRes.builder();
        parentTodoResBuilder
                .todoId(todo.getTodoId())
                .isParent(todo.isParent())
                .startDate(todo.getStartDate().format(formatter))
                .dueDate(todo.getDueDate().format(formatter))
                .content(todo.getContent())
                .childTodoList(new ArrayList<>())
                .assignList(assignMapper.toResDto(todo.getAssignList()));

        if(todo.isParent())
            return parentTodoResBuilder.childTodoList(toResDto(todo.getChildTodoList())).build();
        else
            return parentTodoResBuilder.build();
    }

    public List<ParentTodoRes> toParentDto(List<Todo> todoList){
        if(todoList == null)
            return null;

        List<Long> todoIdList = new ArrayList<>();
        List<ParentTodoRes> todoResList = new ArrayList<>();
        for (Todo todo : todoList) {
            ParentTodoRes parentTodoRes = toParentDto(todo);
            if (todoIdList.contains(parentTodoRes.getTodoId()))
                continue;
            todoResList.add(parentTodoRes);
            todoIdList.add(parentTodoRes.getTodoId());
            todoIdList.addAll(parentTodoRes.getChildTodoList().stream().map(TodoSingleRes::getTodoId).collect(Collectors.toList()));
        }

        return todoResList;
    }

    public TodoSimpleRes toSimpleDto(Assign assign){
        if(assign == null)
            return null;

        Todo todo = assign.getTodo();
        TodoSimpleRes.TodoSimpleResBuilder todoSimplifiedResBuilder = TodoSimpleRes.builder();
        return todoSimplifiedResBuilder
                .assignId(assign.getAssignId())
                .projectId(todo.getProject().getProjectId())
                .projectName(todo.getProject().getProjectName())
                .dueDate(todo.getDueDate().format(formatter))
                .content(todo.getContent())
                .status(String.valueOf(assign.getStatus())).build();
    }

    public List<TodoSimpleRes> toSimpleDto(List<Assign> assignList){
        if(assignList == null)
            return null;

        return assignList.stream().map(this::toSimpleDto).collect(Collectors.toList());
    }

    public TodoSsePostData toSsePostData(TodoSingleRes todoSingleRes){
        if(todoSingleRes == null)
            return null;

        TodoSsePostData.TodoSsePostDataBuilder todoSsePostDataBuilder = TodoSsePostData.builder();
        todoSsePostDataBuilder
                .todoId(todoSingleRes.getTodoId())
                .isParent(todoSingleRes.getIsParent())
                .startDate(todoSingleRes.getStartDate())
                .dueDate(todoSingleRes.getDueDate())
                .content(todoSingleRes.getContent())
                .assignList(todoSingleRes.getAssignList());

        if(!todoSingleRes.getIsParent())
            todoSsePostDataBuilder.parentId(todoSingleRes.getParentId());
        return todoSsePostDataBuilder.build();
    }

    public TodoSseUpdateData toSseUpdateData(TodoSingleRes todoSingleRes){
        if(todoSingleRes == null)
            return null;

        TodoSseUpdateData.TodoSseUpdateDataBuilder todoSseUpdateDataBuilder = TodoSseUpdateData.builder();
        todoSseUpdateDataBuilder
                .todoId(todoSingleRes.getTodoId())
                .isParent(todoSingleRes.getIsParent())
                .startDate(todoSingleRes.getStartDate())
                .dueDate(todoSingleRes.getDueDate())
                .content(todoSingleRes.getContent())
                .assignList(todoSingleRes.getAssignList());

        if(!todoSingleRes.getIsParent())
            todoSseUpdateDataBuilder.parentId(todoSingleRes.getParentId());
        return todoSseUpdateDataBuilder.build();
    }

    public TodoSseDeleteData todoSseDeleteData(Todo todo){
        if(todo == null)
            return null;

        TodoSseDeleteData.TodoSseDeleteDataBuilder todoSseDeleteDataBuilder = TodoSseDeleteData.builder();
        todoSseDeleteDataBuilder.todoId(todo.getTodoId());

        if(!todo.isParent())
            todoSseDeleteDataBuilder.parentId(todo.getParentTodo().getTodoId());
        return todoSseDeleteDataBuilder.build();
    }

}
