package com.linking.annotation.service;

import com.linking.annotation.domain.Annotation;
import com.linking.annotation.dto.*;
import com.linking.annotation.persistence.AnnotationMapper;
import com.linking.annotation.persistence.AnnotationRepository;
import com.linking.block.domain.Block;
import com.linking.block.persistence.BlockRepository;
import com.linking.page_check.domain.PageCheck;
import com.linking.page_check.persistence.PageCheckRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.global.exception.NoAuthorityException;
import com.linking.global.message.ErrorMessage;
import com.linking.sse.EventType;
import com.linking.sse.group.GroupEvent;
import com.linking.sse.page.PageEvent;
import com.linking.sse.page.handler.PageSseHandler;
import com.linking.page.dto.PageRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnotationService {

    private final PageSseHandler pageSseHandler;
    private final ApplicationEventPublisher publisher;
    private final AnnotationRepository annotationRepository;
    private final AnnotationMapper annotationMapper;
    private final PageCheckRepository pageCheckRepository;
    private final BlockRepository blockRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public AnnotationRes createAnnotation(AnnotationCreateReq req, Long userId) {
        log.info("service - createAnnotation");

        Block block = blockRepository.findByFetchAnnotations(req.getBlockId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_BLOCK));

        Participant participant = participantRepository.findByUserAndProjectId(userId, req.getProjectId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PARTICIPANT));

        Annotation annotation = annotationMapper.toEntity(req);
        annotation.setBlock(block);
        annotation.setParticipant(participant);

        AnnotationRes annotationRes = annotationMapper.toDto(annotationRepository.save(annotation));

        // 해당 페이지, 참여자(주석생성한 팀원 제외)로 pageCheck 조회하여 annoNotCount 증가시킴
        List<PageCheck> pageCheckList = pageCheckRepository.findAllByPageId(block.getPage().getId());
        pageCheckList.forEach(pc -> {
            if (pc.getParticipant().getParticipantId() != participant.getParticipantId()) {
                pc.increaseAnnotNotCount();
            }
        });
        // 페이지 들어가 있는 유저 아이디 목록
        Set<Long> enteringUserIds = pageSseHandler.enteringUserIds(block.getPage().getId());

        // 주석 개수 증가 이벤트
        // TODO 프론트에서 pageId를 좀 더 빨리 찾을 수 있도록 groupId를 요청하여 page에서 group을 조회하기 떄문에 select page sql 발생
        publisher.publishEvent(
                GroupEvent.builder()
                    .eventName(EventType.POST_ANNOT_NOT)
                    .projectId(req.getProjectId())
                    .userIds(enteringUserIds)
                    .data(PageRes.builder()
                            .groupId(block.getPage().getGroup().getId())
                            .pageId(block.getPage().getId())
                            .build())
                .build());

        // 주석 생성 이벤트
        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.POST_ANNOT)
                        .pageId(block.getPage().getId())
                        .userId(userId)
                        .data(annotationRes).build()
        );

        return annotationRes;
    }

    @Transactional
    public AnnotationRes updateAnnotation(AnnotationUpdateReq annotationReq, Long userId) {

        log.info("updateAnnotation");
        Annotation annotation = annotationRepository.findById(annotationReq.getAnnotationId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_ANNOTATION));

        // 본인이 작성한 주석 아닌 경우 수정 불가
        Participant writer = annotation.getParticipant();
        if ((writer != null && writer.getUser().getUserId() != userId) || (writer == null)) {
            throw new NoAuthorityException("해당 주석을 수정할 권한이 없습니다.");
        }

        annotation.updateContent(annotationReq.getContent());
        AnnotationRes annotationRes = annotationMapper.toDto(annotation);

        AnnotationUpdateRes annotationUpdateRes = AnnotationUpdateRes.builder()
                .annotationId(annotationRes.getAnnotationId())
                .blockId(annotationRes.getBlockId())
                .content(annotationRes.getContent())
                .lastModified(annotationRes.getLastModified())
                .build();

        // 주석 내용 수정 이벤트
        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.UPDATE_ANNOT)
                        .pageId(annotation.getBlock().getPage().getId())
                        .userId(userId)
                        .data(annotationUpdateRes).build()
        );

        return annotationRes;
    }

    @Transactional
    public void deleteAnnotation(Long annotationId, Long projectId, Long userId) {
        log.info("deleteAnnotation");

        Annotation annotation = annotationRepository.findById(annotationId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_ANNOTATION));

        // 본인이 작성한 주석 아닌 경우 삭제 불가. 주석의 작성자가 탈퇴 또는 프로젝트 나간 경우 삭제 가능
        Participant writer = annotation.getParticipant();
        if (writer != null && writer.getUser().getUserId() != userId) {
            throw new NoAuthorityException("해당 주석을 삭제할 권한이 없습니다.");
        }

        Long groupId = annotation.getBlock().getPage().getGroup().getId();
        Long pageId = annotation.getBlock().getPage().getId();
        Long blockId = annotation.getBlock().getId();
        annotationRepository.delete(annotation);

        Participant participant = participantRepository.findByUserAndProjectId(userId, projectId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PARTICIPANT));

        // 해당 페이지, 참여자(주석생성한 팀원 제외)로 pageCheck 조회하여 annoNotCount 감소시킴
        List<PageCheck> pageCheckList = pageCheckRepository.findAllByPageId(pageId);
        pageCheckList.forEach(pc -> {
            if (pc.getParticipant().getParticipantId() != participant.getParticipantId()) {
                pc.reduceAnnoNotCount();
//                pageCheckRepository.save(pc);
            }
        });

        // 페이지 들어가 있는 유저 아이디 목록
        Set<Long> enteringUserIds = pageSseHandler.enteringUserIds(pageId);

        // 주석 개수 감소 이벤트
        publisher.publishEvent(
                GroupEvent.builder()
                    .eventName(EventType.DELETE_ANNOT_NOT)
                    .projectId(projectId)
                    .userIds(enteringUserIds)
                    .data(PageRes.builder()
                            .groupId(groupId)
                            .pageId(pageId)
                            .build())
                .build());

        // 주석 삭제 이벤트
        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.DELETE_ANNOT)
                        .pageId(pageId)
                        .userId(userId)
                        .data(new AnnotationIdRes(annotationId, blockId)).build()
        );
    }
}
