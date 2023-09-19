package com.linking.page.dto;

import com.linking.block.dto.BlockDetailRes;
import com.linking.page_check.dto.PageCheckRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BlockPageDetailRes extends PageDetailedRes{

    private List<BlockDetailRes> blockResList;

    public BlockPageDetailRes(Long pageId, Long groupId, String title, List<PageCheckRes> pageCheckResList, List<BlockDetailRes> blockResList) {
        super(pageId, groupId, title, pageCheckResList);
        this.blockResList = blockResList;
    }
}
