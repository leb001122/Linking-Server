package com.linking.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageUpdateTitleReq {

    @NotNull
    private Long pageId;

    @NotNull
    private String title;
}
