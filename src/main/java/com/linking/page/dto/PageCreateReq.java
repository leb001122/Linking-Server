package com.linking.page.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.linking.page.domain.Template;
import lombok.*;
import org.mapstruct.EnumMapping;

import javax.validation.constraints.NotNull;

@Data
@Setter(AccessLevel.NONE)
public class PageCreateReq {

    @NotNull
    private Long groupId;
    @NotNull
    private String title;
    @NotNull
    private int order;
    @NotNull
    private Template template;
}
