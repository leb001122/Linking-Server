package com.linking.todo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCreateReq {

    @NotNull
    private int emitterId;

    @NotNull
    private Long projectId;

    private Long parentId;

    @NotNull
    private Boolean isParent;

    @NotNull
    private String startDate;

    @NotNull
    private String dueDate;

    @NotNull
    private String content;

    @NotNull
    @Size(min = 1)
    private List<Long> assignList;

}
