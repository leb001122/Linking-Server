package com.linking.block.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockCreateReq {

    @Setter
    private int order;

    @NotNull
    private Long pageId;

    private String title;
}
