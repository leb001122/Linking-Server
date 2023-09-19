package com.linking.sse.group.handler;

import com.linking.sse.domain.CustomEmitter;
import com.linking.sse.group.GroupEvent;
import com.linking.sse.group.persistence.GroupEmitterInMemoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupSseHandler {

    private static final Long TIMEOUT = 10 * 60 * 1000L;

    private final GroupEmitterInMemoryRepo emitterRepository;

    public SseEmitter connect(Long projectId, Long userId) {

        log.info("[GROUP][CONNECT] projectId = {}, userId = {}", projectId, userId);

        CustomEmitter customEmitter = emitterRepository.save(projectId, new CustomEmitter(userId, new SseEmitter(TIMEOUT)));
        SseEmitter emitter = customEmitter.getSseEmitter();

        emitter.onTimeout(() -> {
            emitter.complete();
        });
        emitter.onCompletion(() -> {
            emitterRepository.deleteEmitter(projectId, customEmitter);
            log.info("[GROUP][REMOVE] projectId = {}, userId = {}", projectId, userId);
        });
        return emitter;
    }

    @EventListener
    public void send(GroupEvent event) {

        Set<CustomEmitter> emittersByProject = emitterRepository.findEmittersByKey(event.getProjectId());
        if (emittersByProject == null) return;

        if (event.getUserId() == null)
            sendEventExceptUserIdList(event, emittersByProject);
        else
            sendEventExceptPublisher(event, emittersByProject);
    }

    private void sendEventExceptPublisher(GroupEvent event, Set<CustomEmitter> emitters) {

        emitters.forEach(emitter -> {
            if (event.getUserId() != emitter.getUserId()) {
                try {
                    emitter.getSseEmitter().send(
                            SseEmitter.event()
                                    .name(event.getEventName())
                                    .data(event.getData()));
                    log.info("send {} event to user {}", event.getEventName(), emitter.getUserId());

                } catch (IOException e) {
                    log.error("Connection reset by peer");
                }

            }
        });
    }

    private void sendEventExceptUserIdList(GroupEvent event, Set<CustomEmitter> emitters) {

        emitters.forEach(emitter -> {
            if (!event.getUserIds().contains(emitter.getUserId())) {
                try {
                    emitter.getSseEmitter().send(
                            SseEmitter.event()
                                    .name(event.getEventName())
                                    .data(event.getData()));
                    log.info("send {} event", event.getEventName());

                } catch (IOException e) {
                    log.error("Connection reset by peer");
                }
            }
        });
    }

    @Async("eventCallExecutor")
    public void removeEmittersByProject(Long projectId) {

        log.info("GroupSseHandler.removeEmittersByProject");

        Set<CustomEmitter> customEmitters = emitterRepository.deleteAllByKey(projectId);

        if (customEmitters != null) {
            for (CustomEmitter customEmitter : customEmitters) {
                if (customEmitter.getSseEmitter() != null)
                    customEmitter.getSseEmitter().complete();
            }
        }
        log.info("[GROUP][REMOVE_ALL] project = {} is removed", projectId);
    }

    @Scheduled(fixedRate = 45000)
    public void ping() {
        Map<Long, Set<CustomEmitter>> all = emitterRepository.getAll();
        all.forEach((key, set) -> {
            set.forEach(ce -> {
                try {
                    ce.getSseEmitter().send(SseEmitter.event()
                            .name("ping")
                            .data("ping"));
                } catch (IOException e) {
                    log.error("Connection reset by peer");
                }
            });
        });
    }
}
