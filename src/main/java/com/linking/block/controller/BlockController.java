package com.linking.block.controller;

import com.linking.block.dto.BlockCloneReq;
import com.linking.block.dto.BlockCreateReq;
import com.linking.block.dto.BlockOrderReq;
import com.linking.block.dto.BlockRes;
import com.linking.block.service.BlockService;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blocks")
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<Object> postBlock(
            @RequestBody @Valid BlockCreateReq req,
            @RequestHeader Long userId
    ) {

        Long blockId = blockService.createBlock(req, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_201, HttpStatus.CREATED, blockId);
    }

    @PutMapping("/order")
    public ResponseEntity<Object> putBlockOrder(
            @RequestBody @Valid BlockOrderReq req,
            @RequestHeader Long userId
    ) {
        log.info("putBlockOrder ========================================================================");
        log.info("pageId = {}, blockIds.size = {}", req.getPageId(), req.getBlockIds().size());
        log.info("req.getBlockIds(0) = {}", req.getBlockIds().get(0));
        boolean res = blockService.updateBlockOrder(req, userId);
        log.info("res = {}", res);
        return ResponseHandler.generateOkResponse(res);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBlock(
            @PathVariable("id") Long blockId,
            @RequestHeader Long userId
    ) {

        blockService.deleteBlock(blockId, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_204, HttpStatus.NO_CONTENT, null);
    }

    @PostMapping("/clone")
    public ResponseEntity cloneBlock(
            @RequestBody BlockCloneReq blockCloneReq,
            @RequestHeader Long userId
    ) {
        Long blockId = blockService.cloneBlock(blockCloneReq, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_201, HttpStatus.CREATED, blockId);
    }
}
