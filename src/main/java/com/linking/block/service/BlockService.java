package com.linking.block.service;

import com.linking.block.persistence.BlockMapper;
import com.linking.block.domain.Block;
import com.linking.block.dto.*;
import com.linking.block.persistence.BlockRepository;
import com.linking.global.exception.BadRequestException;
import com.linking.socket.page.BlockSnapshot;
import com.linking.socket.page.service.PageWebSocketService;
import com.linking.page.domain.Page;
import com.linking.page.domain.Template;
import com.linking.page.persistence.PageRepository;
import com.linking.global.message.ErrorMessage;
import com.linking.sse.page.PageSseEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final PageSseEventPublisher pageSsePublisher;

    private final BlockRepository blockRepository;
    private final BlockMapper blockMapper;

    private final PageRepository pageRepository;

    private final PageWebSocketService pageWebSocketService;

    @Transactional
    public Long createBlock(BlockCreateReq req, Long userId) {

        Page page = pageRepository.findByIdFetchBlocks(req.getPageId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE));

        if (page.getTemplate() == Template.BLANK)
            throw new BadRequestException("cannot add block in Blank template");

        Block block = saveBlock(page, req.getTitle(), null);
        // post Block Event
        pageSsePublisher.publishPostBlockEvent(userId, blockMapper.toDto(block));

        return block.getId();
    }

    @Transactional
    public Long cloneBlock(BlockCloneReq blockCloneReq, Long userId) {

        // 복제 블럭이 생성될 page 조회
        Page page = pageRepository.findById(blockCloneReq.getPageId())
                .orElseThrow(NoSuchElementException::new);

        if (page.getTemplate() == Template.BLANK)
            throw new BadRequestException("cannot add block in Blank Page");

        Block block = saveBlock(page, blockCloneReq.getTitle(), blockCloneReq.getContent());
        // post block event
        pageSsePublisher.publishPostBlockEvent(userId, blockMapper.toDto(block));

        return block.getId();
    }

    private Block saveBlock(Page page, String blockTitle, String blockContent) {
        // block 저장 in db
        Block block = blockRepository.save(new Block(blockTitle, blockContent, page));
        // block snapshot 저장 in memory
        pageWebSocketService.createBlock(page.getId(), block.getId(), new BlockSnapshot(block.getTitle(), block.getContent()));
        return block;
    }

    @Transactional
    public boolean updateBlockOrder(BlockOrderReq req, Long userId) {

        List<Block> blockList = blockRepository.findAllByPageId(req.getPageId());

        boolean isSendEventRequired = false;
        for (Block b : blockList) {
            int newOrder = req.getBlockIds().indexOf(b.getId());
            if (newOrder != b.getBlockOrder()) {
                b.updateOrder(newOrder);
                isSendEventRequired = true;
            }
        }
        if (isSendEventRequired)
            pageSsePublisher.publishBlockOrderEvent(userId, req.getPageId(), req.getBlockIds());

        return true;
    }

    @Transactional
    public void deleteBlock(Long blockId, Long userId) {
    // todo pageId 받아서 page로 조회
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_BLOCK));

        Long pageId = block.getPage().getId();
        blockRepository.delete(block);

        // block snapshot 삭제
        pageWebSocketService.deleteBlockSnapshot(pageId, blockId);
        // order 재조정
        List<Block> blockList = blockRepository.findAllByPageId(pageId);
        int order = 0;
        for (Block b : blockList) b.updateOrder(order++);

        // delete Block Event
        pageSsePublisher.publishDeleteBlockEvent(userId, pageId, blockMapper.toBlockIdResDto(blockId));
    }
}

