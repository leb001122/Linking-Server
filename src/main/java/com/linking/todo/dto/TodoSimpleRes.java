package com.linking.todo.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoSimpleRes {

    private Long assignId;
    private Long projectId;
    private String projectName;
    private String dueDate;
    private String content;
    private String status;

}
