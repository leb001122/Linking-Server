package com.linking.global.common;

import com.linking.participant.domain.Participant;
import com.linking.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@AllArgsConstructor
public class ChattingSession {

    private Project project;
    private Participant participant;
    private Boolean isFocusing;
    private WebSocketSession webSocketSession;

    public void setIsFocusing(Boolean isFocusing){
        this.isFocusing = isFocusing;
    }

}
