package com.linking.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupNameReq {

    @NotNull
    private Long groupId;
    @NotNull
    private String name;
}
