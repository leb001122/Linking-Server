package com.linking.page.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiffStr {

    private int type;
    private int diffStartIndex;
    private int diffEndIndex;
    private String subStr;

    public DiffStr(int type, int diffStartIndex, int diffEndIndex, String subStr) {
        this.type = type;
        this.diffStartIndex = diffStartIndex;
        this.diffEndIndex = diffEndIndex;
        this.subStr = subStr;
    }

    public DiffStr(int type, int diffStartIndex, String subStr) {
        this.type = type;
        this.diffStartIndex = diffStartIndex;
        this.diffEndIndex = -1;
        this.subStr = subStr;
    }
}
