package com.linking.socket.page;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageSocketMessageReq {

    @NotNull
    private Integer editorType;

    private Long blockId;

    @NotNull
    @Setter
    private String docs;

    public PageSocketMessageReq(Integer editorType, Long blockId, String docs) {
        this.editorType = editorType;
        this.blockId = blockId;
        this.docs = docs;
    }


//    @NotNull
//    private Integer editorType;
//
//    private Long blockId;
//
//    @NotNull
//    private String inputType;
//
//    @NotNull
//    private Integer index;
//
//    private String character;
//
//    public TextInputMessage(Integer editorType, Long blockId, String inputType, Integer index, String character) {
//        this.editorType = editorType;
//        this.blockId = blockId;
//        this.inputType = inputType;
//        this.index = index;
//        this.character = character;
//    }
}
