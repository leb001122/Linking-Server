package com.linking.socket.page.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class BlankPageSnapshotRepo {

    /**
     * key : pageId
     */
    private final Map<Long, String> document = new ConcurrentHashMap<>();

    // todo 페이지 생성 or 어플리케이션 초기화
    public void put(Long pageId, String doc) {
        document.put(pageId, doc);
    }

    public String get(Long pageId) {
        String str = document.get(pageId);
        if (str == null) {
            str = "";
            document.replace(pageId, str);
        }
        return str;
    }

    public void replace(Long pageId, String doc) {
        document.replace(pageId, doc);
    }

    public boolean delete(Long pageId) {
        if(document.remove(pageId) != null) return true;
        return false;
    }

    public int size() {
        return document.size();
    }

    public void clear() {
        document.clear();
    }
}
