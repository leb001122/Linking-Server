package com.linking.group.controller;

import com.linking.global.common.ResponseHandler;
import com.linking.sse.group.handler.GroupSseHandler;
import com.linking.group.dto.*;
import com.linking.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupSseHandler groupSseHandler;
    private final GroupService groupService;

    @GetMapping("/list")
    public ResponseEntity<List<GroupRes>> getGroups(
            @RequestParam("projectId") Long projectId,
            @RequestHeader Long userId
    ){
        List<GroupRes> allGroups = groupService.findAllGroups(projectId, userId);
        return ResponseHandler.generateOkResponse(allGroups);
    }

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeGroup(
            @RequestParam("projectId") Long projectId,
            @RequestHeader Long userId
    ){
        SseEmitter sseEmitter = groupSseHandler.connect(projectId, userId);
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            log.error("cannot send event");
        }
        return ResponseEntity.ok(sseEmitter);
    }

    @PostMapping
    public ResponseEntity<Object> postGroup(
            @RequestBody @Valid GroupCreateReq req,
            @RequestHeader Long userId
    ){
        GroupRes res = groupService.createGroup(req, userId);
        return ResponseHandler.generateCreatedResponse(res);
    }

    @PutMapping
    public ResponseEntity<Object> putGroupName(
            @RequestBody @Valid GroupNameReq req,
            @RequestHeader Long userId
    ) {

        Boolean res = groupService.updateGroupName(req, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_200, HttpStatus.OK, res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGroup(
            @PathVariable("id") Long groupId,
            @RequestHeader Long userId
    ) {

        groupService.deleteGroup(groupId, userId);
        return ResponseHandler.generateNoContentResponse();
    }

    @PutMapping("/order")
    public ResponseEntity<Object> putDocumentOrder(
            @RequestBody @Valid List<GroupOrderReq> req,
            @RequestHeader Long userId
    ) {

        boolean res = groupService.updateDocumentsOrder(req);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_200, HttpStatus.OK, res);
    }

    @GetMapping("/blockpages/{projectId}")
    public ResponseEntity findBlockPages(
            @PathVariable Long projectId
    ) {
        List<GroupRes> res = groupService.getBlockPages(projectId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_200, HttpStatus.OK, res);
    }
}