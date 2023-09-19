package com.linking.chat.dto;

import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatReq {

    @NotNull
    private Long userId;

    @NotNull
    private Long projectId;

    private String content;

    @NotNull
    private String sentDatetime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReqType reqType;

    @NotNull
    private Boolean isFocusing;

}
