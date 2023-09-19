package com.linking.chatroom.controller;


import com.linking.chat.service.ChatService;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatService chatService;

    @GetMapping("/{id}/chat-list")
    public ResponseEntity<Object> getMessages(@PathVariable Long id, Pageable pageable){
        return ResponseHandler.generateOkResponse(chatService.getRecentChatList(id, pageable));
    }

}
