package com.linking.page.dto;

import com.linking.page_check.dto.PageCheckRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@SuperBuilder
@NoArgsConstructor
public class BlankPageDetailRes extends PageDetailedRes{

    private String content;

    public BlankPageDetailRes(Long pageId, Long groupId, String title, List<PageCheckRes> pageCheckResList, String content) {
        super(pageId, groupId, title, pageCheckResList);
        this.content = content;
    }
}
