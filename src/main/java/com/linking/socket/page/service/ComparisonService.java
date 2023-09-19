package com.linking.socket.page.service;

import com.linking.page.domain.DiffStr;
import com.linking.global.util.StringComparison;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComparisonService {

    public DiffStr compare(String oldStr, String newStr) {

        log.info("=====================================================");

        DiffStr diffStr = StringComparison.compareString(oldStr, newStr);


        if (diffStr == null) {
            log.info("Not Modified");
            return null;
        }

        log.info("oldStr = {}", oldStr);
        log.info("newStr = {}", newStr);
        log.info("type : {}, start : {}, end : {}, subStr : {}", diffStr.getType(), diffStr.getDiffStartIndex(), diffStr.getDiffEndIndex(), diffStr.getSubStr());

        return diffStr;
    }
}
