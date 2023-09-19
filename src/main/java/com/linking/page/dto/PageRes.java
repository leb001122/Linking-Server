package com.linking.page.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linking.page.domain.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class  PageRes {

    private Long pageId;
    private Long groupId;
    private String title;
    private Template template;
    private Integer annoNotCnt;
}
