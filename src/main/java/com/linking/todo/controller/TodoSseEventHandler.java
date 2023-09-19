package com.linking.todo.controller;

import com.linking.assign.dto.AssignSseUpdateData;
import com.linking.todo.dto.TodoSseDeleteData;
import com.linking.todo.dto.TodoSsePostData;
import com.linking.todo.dto.TodoSseUpdateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TodoSseEventHandler {

    private final TodoSseHandler todoSseHandler;

    @Async("eventCallExecutor")
    public void postParent(int emitterId, Long projectId, TodoSsePostData todoSsePostData){
        todoSseHandler.send(emitterId, projectId, "postParent", todoSsePostData);
    }

    @Async("eventCallExecutor")
    public void postChild(int emitterId, Long projectId, TodoSsePostData todoSsePostData){
        todoSseHandler.send(emitterId, projectId, "postChild", todoSsePostData);
    }

    @Async("eventCallExecutor")
    public void updateParent(int emitterId, Long projectId, TodoSseUpdateData todoSseUpdateData){
        todoSseHandler.send(emitterId, projectId, "updateParent", todoSseUpdateData);
    }

    @Async("eventCallExecutor")
    public void updateChild(int emitterId, Long projectId, TodoSseUpdateData todoSseUpdateData){
        todoSseHandler.send(emitterId, projectId, "updateChild", todoSseUpdateData);
    }

    @Async("eventCallExecutor")
    public void updateParentStatus(int emitterId, Long projectId, AssignSseUpdateData assignSseUpdateData){
        todoSseHandler.send(emitterId, projectId, "updateParentStatus", assignSseUpdateData);
    }

    @Async("eventCallExecutor")
    public void updateChildStatus(int emitterId, Long projectId, AssignSseUpdateData assignSseUpdateData){
        todoSseHandler.send(emitterId, projectId, "updateChildStatus", assignSseUpdateData);
    }

    @Async("eventCallExecutor")
    public void deleteParent(int emitterId, Long projectId, TodoSseDeleteData todoSseDeleteData){
        todoSseHandler.send(emitterId, projectId, "deleteParent", todoSseDeleteData);
    }

    @Async("eventCallExecutor")
    public void deleteChild(int emitterId, Long projectId, TodoSseDeleteData todoSseDeleteData){
        todoSseHandler.send(emitterId, projectId, "deleteChild", todoSseDeleteData);
    }

}
