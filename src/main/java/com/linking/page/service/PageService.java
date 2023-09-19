package com.linking.page.service;

import com.linking.block.dto.BlockDetailRes;
import com.linking.annotation.domain.Annotation;
import com.linking.annotation.dto.AnnotationRes;
import com.linking.annotation.persistence.AnnotationMapper;
import com.linking.block.domain.Block;
import com.linking.block.persistence.BlockMapper;
import com.linking.block.persistence.BlockRepository;
import com.linking.global.message.ErrorMessage;
import com.linking.page.dto.PageTitleReq;
import com.linking.socket.page.BlockSnapshot;
import com.linking.socket.page.service.PageWebSocketService;
import com.linking.sse.EventType;
import com.linking.sse.group.GroupEvent;
import com.linking.group.domain.Group;
import com.linking.group.persistence.GroupRepository;
import com.linking.page.dto.PageCreateReq;
import com.linking.page.dto.PageDetailedRes;
import com.linking.page.dto.PageRes;
import com.linking.page_check.domain.PageCheck;
import com.linking.page_check.dto.PageCheckRes;
import com.linking.page_check.persistence.PageCheckMapper;
import com.linking.page_check.persistence.PageCheckRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.sse.page.PageEvent;
import com.linking.page.domain.Page;
import com.linking.page.domain.Template;
import com.linking.page.persistence.PageMapper;
import com.linking.page.persistence.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageService {

    private final ApplicationEventPublisher publisher;

    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    private final GroupRepository groupRepository;

    private final PageCheckRepository pageCheckRepository;
    private final PageCheckMapper pageCheckMapper;

    private final ParticipantRepository participantRepository;

    private final BlockRepository blockRepository;
    private final BlockMapper blockMapper;

    private final AnnotationMapper annotationMapper;

    private final PageWebSocketService pageWebSocketService;

    @Transactional
    public PageDetailedRes getPage(Long pageId, Set<Long> enteringUserIds) {

        // toMany는 하나만 Fetch join 가능
        Page page = pageRepository.findByIdFetchPageChecks(pageId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE));

        List<PageCheckRes> pageCheckResList = toPageCheckResList(page.getPageCheckList(), enteringUserIds);

        if (page.getTemplate() == Template.BLANK) {
            page.setContent(pageWebSocketService.findBlankPageSnapshot(pageId));
            return pageMapper.toDto(page, pageCheckResList);

        } else if (page.getTemplate() == Template.BLOCK) {
            List<Block> blockList = blockRepository.findAllByPageIdFetchAnnotations(page.getId());
            Map<Long, BlockSnapshot> blockPageSnapshot = pageWebSocketService.findBlockPageSnapshot(pageId);

            blockList.forEach(block -> {
                BlockSnapshot blockSnapshot = blockPageSnapshot.get(block.getId());
                block.setTitle(blockSnapshot.getTitle());
                block.setContent(blockSnapshot.getContent());
            });
            List<BlockDetailRes> blockResList = toBlockResList(blockList);
            return pageMapper.toDto(page, blockResList, pageCheckResList);
        }
        return null;
    }


    private List<PageCheckRes> toPageCheckResList(List<PageCheck> pageCheckList, Set<Long> enteringUserIds) {
        List<PageCheckRes> pageCheckResList = new ArrayList<>();

        pageCheckList.forEach(pageCheck -> {
            PageCheckRes pageCheckRes = pageCheckMapper.toDto(pageCheck);
            if (enteringUserIds.contains(pageCheck.getParticipant().getUser().getUserId()))
                pageCheckRes.setIsEntering(true);
            else
                pageCheckRes.setIsEntering(false);
            pageCheckResList.add(pageCheckRes);
        });

//         userName 순으로 정렬
        return pageCheckResList.stream()
                .sorted(Comparator.comparing(PageCheckRes::getUserName))
                .collect(Collectors.toList());
    }

    private List<BlockDetailRes> toBlockResList(List<Block> blockList) {

        if (blockList.isEmpty())
            return blockMapper.toDummyDto();

        List<BlockDetailRes> blockResList = new ArrayList<>();

        for (Block block : blockList) {
            List<AnnotationRes> annotationResList = new ArrayList<>();
            List<Annotation> annotations = block.getAnnotationList();
            if (annotations.isEmpty())
                annotationResList.add(annotationMapper.toDummyDto());
            else {
                for (Annotation annotation : block.getAnnotationList())
                    annotationResList.add(annotationMapper.toDto(annotation));
            }
            blockResList.add(blockMapper.toDto(block, annotationResList));
        }
        return blockResList;
    }

    @Transactional
    public PageRes createPage(PageCreateReq req, Long userId) {

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_GROUP));

        Page page = pageMapper.toEntity(req, group);
        Integer pageOrder = pageRepository.findMaxPageOrder(group.getId());
        if (pageOrder != null) pageOrder++;
        else pageOrder = 0;

        pageRepository.save(page);

        if (page.getTemplate() == Template.BLANK)
            pageWebSocketService.createBlankPage(page.getId(), page.getContent());

        else if (page.getTemplate() == Template.BLOCK)
            pageWebSocketService.createBlockPage(page.getId());

        // 팀원 마다 pageCheck create
        List<Participant> participants = participantRepository.findAllByProjectId(group.getProject().getProjectId());
        for (Participant participant : participants) {
            PageCheck pageCheck = new PageCheck(participant, page);
            pageCheckRepository.save(pageCheck);
        }

        PageRes pageRes = pageMapper.toDto(pageRepository.save(page), 0);

        publisher.publishEvent(
                GroupEvent.builder()
                    .eventName(EventType.POST_PAGE)
                    .projectId(group.getProject().getProjectId())
                    .userId(userId)
                    .data(pageRes)
                    .build());

        return pageRes;
    }

    public void deletePage(Long pageId, Long userId) throws NoSuchElementException{

        log.info("deletePage - {}", this.getClass().getSimpleName());

        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE));
        Long projectId = page.getGroup().getProject().getProjectId();
        Long groupId = page.getGroup().getId();
        Template template = page.getTemplate();
        pageRepository.delete(page);

        publisher.publishEvent(
                GroupEvent.builder()
                    .eventName(EventType.DELETE_PAGE)
                    .projectId(projectId)
                    .userId(userId)
                    .data(PageRes.builder()
                            .groupId(groupId)
                            .pageId(pageId)
                            .build())
                    .build());

        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.DELETE_PAGE)
                        .pageId(pageId)
                        .userId(userId)
                        .data(pageId).build());


        // 페이지 순서를 0부터 재정렬
        try {
            List<Page> pageList = pageRepository.findAllByGroupId(groupId);
            int order = 0;
            for (Page p : pageList) {
                if (p.getPageOrder() != order) {
                    p.updateOrder(order);
                    pageRepository.save(p);
                }
                order++;
            }

            if (pageWebSocketService.deletePageSnapshot(page.getId(), template)) {
                log.info("delete pageContent snapshot => PAGE ID = {}", page.getId());
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean checkPageExist(Long pageId) {

        pageRepository.findById(pageId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE));
        return true;
    }

    @Transactional
    public boolean updatePageTitle(PageTitleReq req, Long userId) {

        Page page = pageRepository.findById(req.getPageId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE));

        if (page.getTitle().equals(req)) return true;

        page.setTitle(req.getTitle());

        // 페이지 sse에 이벤트 전송 - 본인 포함해서 보내야함.
        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.PUT_PAGE_TITLE)
                        .pageId(page.getId())
                        .userId(-1L)
                        .data(PageRes.builder()
                                .pageId(page.getId())
                                .title(page.getTitle())
                                .build())
                        .build()
        );

        // 그룹 sse에 이벤트 전송
        publisher.publishEvent(
                GroupEvent.builder()
                        .eventName(EventType.PUT_PAGE_TITLE)
                        .projectId(req.getProjectId())
                        .userId(userId)
                        .data(PageRes.builder()
                                .groupId(req.getGroupId())
                                .pageId(page.getId())
                                .title(page.getTitle())
                                .build())
                        .build()
        );

        return true;
    }
}
