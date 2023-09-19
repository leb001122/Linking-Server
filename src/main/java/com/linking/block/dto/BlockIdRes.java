package com.linking.block.dto;

import lombok.Getter;

@Getter
public class BlockIdRes {

    private Long blockId;

    public BlockIdRes(Long blockId) {
        this.blockId = blockId;
    }
}
