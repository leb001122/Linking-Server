package com.linking.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linking.page.dto.PageRes;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupRes {

    private Long projectId;
    private Long groupId;
    private String name;
    private List<PageRes> pageResList;
}
