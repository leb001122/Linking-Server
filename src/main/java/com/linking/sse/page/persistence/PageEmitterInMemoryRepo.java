package com.linking.sse.page.persistence;

import com.linking.sse.domain.CustomEmitter;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PageEmitterInMemoryRepo {

    /**
     * key : pageId
     */
    private final Map<Long, Set<CustomEmitter>> pageSubscriber = new ConcurrentHashMap<>();

    public Set<CustomEmitter> findEmittersByKey(Long key) {

        System.out.println("PageEmitterInMemoryRepoImpl.findEmittersByKey");

        Set<CustomEmitter> emitters = this.pageSubscriber.get(key);

        if (emitters == null) return null;
        return emitters;
    }

    public CustomEmitter save(Long key, CustomEmitter customEmitter) {

        Set<CustomEmitter> emitters = this.findEmittersByKey(key);

        if (emitters == null) {
            emitters = Collections.synchronizedSet(new HashSet<>());
            emitters.add(customEmitter);
            this.pageSubscriber.put(key, emitters);

        } else {
            emitters.add(customEmitter);
        }
        return customEmitter;
    }

    public boolean deleteEmitter(Long key, CustomEmitter customEmitter) {
        Set<CustomEmitter> emittersByKey = this.findEmittersByKey(key);
        return emittersByKey.remove(customEmitter);
    }

    public Set<CustomEmitter> deleteAllByKey(Long key) {
        Set<CustomEmitter> emittersByKey = this.findEmittersByKey(key);
        pageSubscriber.remove(key);
        return emittersByKey;
    }

    public int size() {
        return pageSubscriber.size();
    }

    public Map<Long, Set<CustomEmitter>> getAll() {
        return pageSubscriber;
    }
}
