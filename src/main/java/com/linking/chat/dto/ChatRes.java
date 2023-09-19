package com.linking.chat.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRes {

    private String firstName;
    private String userName;
    private String content;
    private String sentDatetime;

}
