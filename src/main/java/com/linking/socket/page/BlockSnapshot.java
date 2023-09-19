package com.linking.socket.page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockSnapshot {

    private String title;
    private String content;

    public BlockSnapshot(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
