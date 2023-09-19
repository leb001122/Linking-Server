package com.linking.page.controller;

import com.linking.page.dto.PageTitleReq;
import com.linking.page.service.PageService;
import com.linking.page_check.service.PageCheckService;
import com.linking.global.common.ResponseHandler;
import com.linking.page.dto.PageCreateReq;
import com.linking.page.dto.PageDetailedRes;
import com.linking.page.dto.PageRes;
import com.linking.sse.page.handler.PageSseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
@Slf4j
public class PageController extends TextWebSocketHandler {

    private final PageSseHandler pageSseHandler;
    private final PageService pageService;
    private final PageCheckService pageCheckService;

    @GetMapping("/{id}")
    public ResponseEntity<PageDetailedRes> getPage(
            @RequestHeader(value = "projectId") Long projectId,
            @PathVariable("id") Long pageId,
            @RequestHeader Long userId
    ) {
        pageCheckService.updatePageChecked(pageId, projectId, userId, "enter");
        PageDetailedRes res = pageService.getPage(pageId, pageSseHandler.enteringUserIds(pageId));

        return ResponseHandler.generateOkResponse(res);
    }

    @GetMapping("/subscribe/{id}")
    public ResponseEntity<SseEmitter> subscribePage(
            @PathVariable("id") Long pageId,
            @RequestHeader Long userId
    ) {
        pageService.checkPageExist(pageId);

        SseEmitter sseEmitter = pageSseHandler.connect(pageId, userId);
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
            log.info("** send connect event userID = {}", userId);
        } catch (IOException e) {
            log.error("cannot send event");
        }
        return ResponseEntity.ok(sseEmitter);
    }

    @PostMapping
    public ResponseEntity<Object> postPage(
            @RequestBody @Valid PageCreateReq pageCreateReq,
            @RequestHeader Long userId
    ) {
        PageRes res = pageService.createPage(pageCreateReq, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_201, HttpStatus.CREATED, res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePage(
            @PathVariable("id") Long pageId,
            @RequestHeader Long userId
    ) {
        pageService.deletePage(pageId, userId);
        pageSseHandler.removeEmittersByPage(pageId);
        return ResponseHandler.generateNoContentResponse();
    }

    @GetMapping("/unsubscribe/{id}")
    public void unsubscribePage(
            @RequestHeader(value = "projectId") Long projectId,
            @PathVariable("id") Long pageId,
            @RequestHeader Long userId
    ) {
        pageSseHandler.onClose(userId, pageId);
        pageCheckService.updatePageChecked(pageId, projectId, userId, "leave");
    }

    @PutMapping
    public ResponseEntity<Object> putPageTitle(
            @RequestBody PageTitleReq pageTitleReq,
            @RequestHeader Long userId
    ) {
        boolean res = pageService.updatePageTitle(pageTitleReq, userId);
        return ResponseHandler.generateOkResponse(res);
    }
}

