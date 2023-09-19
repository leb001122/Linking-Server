package com.linking.sse.page;

import com.linking.block.dto.BlockRes;
import com.linking.sse.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PageSseEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishPostBlockEvent(Long publisherId, BlockRes blockRes) {

        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.POST_BLOCK)
                        .pageId(blockRes.getPageId())
                        .userId(publisherId)
                        .data(blockRes)
                .build()
        );
    }

    public void publishDeleteBlockEvent(Long publisherId, Long pageId, BlockRes blockRes) {

        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.DELETE_BLOCK)
                        .pageId(pageId)
                        .userId(publisherId)
                        .data(blockRes)
                .build()
        );
    }

    public void publishBlockOrderEvent(Long publisherId, Long pageId, List<Long> blockIds) {

        publisher.publishEvent(
                PageEvent.builder()
                        .eventName(EventType.PUT_BLOCK_ORDER)
                        .pageId(pageId)
                        .userId(publisherId)
                        .data(blockIds)
                .build()
        );
    }
}