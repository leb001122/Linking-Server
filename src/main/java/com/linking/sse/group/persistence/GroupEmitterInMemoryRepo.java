package com.linking.sse.group.persistence;

import com.linking.sse.domain.CustomEmitter;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GroupEmitterInMemoryRepo {

    /**
     * key : projectId
     */
    private final Map<Long, Set<CustomEmitter>> groupSubscriber = new ConcurrentHashMap<>();

    public Set<CustomEmitter> findEmittersByKey(Long key) {

        System.out.println("GroupEmitterInMemoryRepoImpl.findEmittersByKey");

        Set<CustomEmitter> emitters = this.groupSubscriber.get(key);

        if (emitters == null) return null;
        return emitters;
    }

    public CustomEmitter save(Long key, CustomEmitter customEmitter) {

        Set<CustomEmitter> emitters = this.findEmittersByKey(key);

        if (emitters == null) {
            emitters = Collections.synchronizedSet(new HashSet<>());
            emitters.add(customEmitter);
            this.groupSubscriber.put(key, emitters);

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
        groupSubscriber.remove(key);
        return emittersByKey;
    }

    public Map<Long, Set<CustomEmitter>> getAll() {
        return groupSubscriber;
    }
}
