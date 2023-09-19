package com.linking.page_check.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageCheckUpdateRes {

    private Long userId;
    private Boolean isChecked;
    private String lastChecked;
}
