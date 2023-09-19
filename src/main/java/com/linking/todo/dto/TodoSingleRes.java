package com.linking.todo.dto;

import com.linking.assign.dto.AssignRes;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoSingleRes {

    private Long todoId;
    private Boolean isParent;
    private Long parentId;
    private String startDate;
    private String dueDate;
    private String content;
    private List<AssignRes> assignList;

}
