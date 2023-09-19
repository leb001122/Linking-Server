package com.linking.page_check.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageCheckRes {

    private Long pageCheckId;
    private Long pageId;
    private Boolean isEntering;
    private Boolean isChecked;
    private String lastChecked;
    private Long userId;
    private String userName;

    public void setIsEntering(boolean isEntering) {
        this.isEntering = isEntering;
    }
}
