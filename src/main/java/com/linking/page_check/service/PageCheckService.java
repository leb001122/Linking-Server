package com.linking.page_check.service;

import com.linking.page_check.dto.PageCheckUpdateRes;
import com.linking.page_check.persistence.PageCheckMapper;
import com.linking.page_check.persistence.PageCheckRepository;
import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.global.message.ErrorMessage;
import com.linking.group.domain.Group;
import com.linking.group.persistence.GroupRepository;
import com.linking.sse.EventType;
import com.linking.sse.page.PageEvent;
import com.linking.page.domain.Page;
import com.linking.page_check.domain.PageCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageCheckService {

    private final ApplicationEventPublisher publisher;
    private final PageCheckRepository pageCheckRepository;
    private final PageCheckMapper pageCheckMapper;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;

    public void updatePageChecked(Long pageId, Long projectId, Long userId, String event) {
        log.info("updatePageChecked test" + Thread.currentThread());

        // 팀원 조회
        Participant participant = participantRepository.findByUserAndProjectId(userId, projectId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PARTICIPANT));
        // 페이지 체크 조회
        PageCheck pageCheck = pageCheckRepository.findByPageAndPartId(pageId, participant.getParticipantId())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.NO_PAGE_CHECK));

        pageCheck.updateLastChecked(); // 마지막 열람 시간 업뎃
        if (event.equals("enter"))
            pageCheck.resetAnnoNotCount(); // 주석 알림 개수 리셋

        PageCheckUpdateRes pageCheckUpdateRes = pageCheckMapper.toPageCheckUpdateDto(pageCheckRepository.save(pageCheck));

        PageEvent.PageEventBuilder pageEvent = PageEvent.builder();
        pageEvent
                .pageId(pageId)
                .userId(userId)
                .data(pageCheckUpdateRes).build();

        if (event.equals("leave")) {
            pageEvent
                    .eventName(EventType.PAGE_LEAVE).build();
        }
        else if (event.equals("enter")) {
            pageEvent
                    .eventName(EventType.PAGE_ENTER).build();
        }
        publisher.publishEvent(pageEvent.build());
    }

    // WHEN : participant 생성
    // DO : page 마다 해당 participant 의 pageCheck 생성
    public void createPageCheck(Long projectId, List<Participant> newPartList) {
        log.info("================================== createPageCheck");

        // todo page 에도 project 연관 걸면 좋을 것 같음.
        List<Group> groups = groupRepository.findAllByProjectId(projectId);
        for (Group group : groups) {
            for (Page page : group.getPageList()) {
                for (Participant participant : newPartList) {
                    pageCheckRepository.save(new PageCheck(participant, page));
                }
            }
        }
    }

    // key : pageId, value : annoNotCnt
    public Map<Long, Integer> getAnnoNotCntByParticipant(Long projectId, Long userId) {

        List<PageCheck> pageCheckList = pageCheckRepository.findAllByParticipant(userId, projectId);

        Map<Long, Integer> annoNotCntMap = new HashMap<>();
        pageCheckList.forEach(pc -> {
            annoNotCntMap.put(pc.getPage().getId(), pc.getAnnoNotCount());
        });
        return annoNotCntMap;
    }
}
