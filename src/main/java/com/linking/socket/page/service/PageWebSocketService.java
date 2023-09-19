package com.linking.socket.page.service;

import com.linking.block.domain.Block;
import com.linking.page.domain.DiffStr;
import com.linking.page.domain.Page;
import com.linking.page.domain.Template;
import com.linking.page.persistence.PageRepository;
import com.linking.socket.page.BlockSnapshot;
import com.linking.socket.page.PageSocketMessageReq;
import com.linking.socket.page.PageSocketMessageRes;
import com.linking.socket.page.TextSendEvent;
import com.linking.socket.page.persistence.BlankPageSnapshotRepo;
import com.linking.socket.page.persistence.BlockPageSnapshotRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageWebSocketService {

    private static final int PAGE_CONTENT = 0;
    private static final int BLOCK_TITLE = 1;
    private static final int BLOCK_CONTENT = 2;

    private final ApplicationEventPublisher publisher;

    private final BlankPageSnapshotRepo blankPageSnapshotRepo;
    private final BlockPageSnapshotRepo blockPageSnapshotRepo;

    private final PageRepository pageRepository;
    private final ComparisonService comparisonService;

    // todo 페이지 존재하는 지 확인
    public boolean isExistPage(Long pageId) {
        Optional<Page> page = pageRepository.findById(pageId);
        return true;
    }

    public void handleTextMessage(Map<String, Object> attributes, PageSocketMessageReq messageReq) {

        Long pageId = (Long) attributes.get("pageId");
        String sessionId = (String) attributes.get("sessionId");

        String oldStr = "";
        String newStr = messageReq.getDocs();
        if (newStr.equals("<br>")){
            messageReq.setDocs("");
        }
        DiffStr diffStr = null;

        switch (messageReq.getEditorType()) {

            case PAGE_CONTENT:
                oldStr = blankPageSnapshotRepo.get(pageId);
                // 비교
                diffStr = comparisonService.compare(oldStr, newStr);
                if (diffStr != null) {
                    // replace
                    blankPageSnapshotRepo.replace(pageId, newStr);
                    // 전송
                    publisher.publishEvent(constructEvent(sessionId, pageId, -1L, diffStr, PAGE_CONTENT));
                }
                break;

            case BLOCK_TITLE:
                oldStr = blockPageSnapshotRepo.findByPageAndBlockId(pageId, messageReq.getBlockId()).getTitle();
                // 비교
                diffStr = comparisonService.compare(oldStr, newStr);
                if (diffStr != null) {
                    // replace
                    blockPageSnapshotRepo.replaceTitle(pageId, messageReq.getBlockId(), newStr);
                    // 전송
                    publisher.publishEvent(constructEvent(sessionId, pageId, messageReq.getBlockId(), diffStr, BLOCK_TITLE));
                }
                break;

            case BLOCK_CONTENT:
                oldStr = blockPageSnapshotRepo.findByPageAndBlockId(pageId, messageReq.getBlockId()).getContent();
                // 비교
                diffStr = comparisonService.compare(oldStr, newStr);
                if (diffStr != null) {
                    // replace
                    blockPageSnapshotRepo.replaceContent(pageId, messageReq.getBlockId(), newStr);
                    // 전송
                    publisher.publishEvent(constructEvent(sessionId, pageId, messageReq.getBlockId(), diffStr, BLOCK_CONTENT));
                }
                break;
        }
    }

    private TextSendEvent constructEvent(String sessionId, Long pageId, Long blockId, DiffStr diffStr, int editorType) {

        TextSendEvent event = TextSendEvent.builder()
            .sessionId(sessionId)
            .pageId(pageId)
            .pageSocketMessageRes(
                    PageSocketMessageRes.builder()
                            .editorType(editorType)
                            .pageId(pageId)
                            .blockId(blockId)
                            .diffStr(diffStr)
                            .build())
        .build();

        return event;
    }

    @PostConstruct
    public void initPages() {

        List<Page> allPages = pageRepository.findAll();
        for (Page page : allPages) {
            if (page.getTemplate() == Template.BLANK) {
                blankPageSnapshotRepo.put(page.getId(), page.getContent());
            }
            else if (page.getTemplate() == Template.BLOCK) {
                blockPageSnapshotRepo.putPage(page.getId());
                for (Block block : page.getBlockList()) {
                    blockPageSnapshotRepo.putBlock(page.getId(), block.getId(), new BlockSnapshot(block.getTitle(), block.getContent()));
                }
            }
        }
        log.info("빈 페이지 Snapshot size = {}", blankPageSnapshotRepo.size());
        log.info("블럭 페이지 Snapshot size = {}", blockPageSnapshotRepo.size());

    }

    public boolean deletePageSnapshot(Long pageId, Template template) {

        if (template == Template.BLANK) {
            log.info("deletePageSnapshot => pageId = {}, template = {}", pageId, template);
            return blankPageSnapshotRepo.delete(pageId);

        } else if (template == Template.BLOCK){
            log.info("deletePageSnapshot => pageId = {}, template = {}", pageId, template);
            return blockPageSnapshotRepo.deletePage(pageId);
        }

        return false;
    }

    public boolean deleteBlockSnapshot(Long pageId, Long blockId) {

        log.info("deleteBlockSnapshot => pageId = {}, blockId = {}", pageId, blockId);
        return blockPageSnapshotRepo.deleteBlock(pageId, blockId);
    }

    // blank page

    public void createBlankPage(Long pageId, String content) {
        blankPageSnapshotRepo.put(pageId, content);
        log.info("create 빈 페이지");
    }

    public String findBlankPageSnapshot(Long pageId) {
        return blankPageSnapshotRepo.get(pageId);
    }

    // block page

    public void createBlockPage(Long pageId) {
        blockPageSnapshotRepo.putPage(pageId);
        log.info("create 블럭 페이지");
    }

    public void createBlock(Long pageId, Long blockId, BlockSnapshot blockSnapshot) {
        blockPageSnapshotRepo.putBlock(pageId, blockId, blockSnapshot);
        log.info("create 블럭");
    }

    public Map<Long, BlockSnapshot> findBlockPageSnapshot(Long pageId) {
        return blockPageSnapshotRepo.findByPageId(pageId);
    }
}
