package com.linking.sse;

public interface EventType {

    // 그룹 리스트 sse
    String POST_GROUP = "postGroup";
    String PUT_GROUP_NAME = "putGroupName";
    String DELETE_GROUP = "deleteGroup";
    String POST_PAGE = "postPage";
    String POST_ANNOT_NOT = "postAnnotation";
    String DELETE_ANNOT_NOT = "deleteAnnotation";

    // 페이지 sse
    String PAGE_ENTER = "enter";
    String PAGE_LEAVE = "leave";
    String POST_BLOCK = "postBlock";
    String PUT_BLOCK_ORDER = "putBlockOrder";
    String DELETE_BLOCK = "deleteBlock";
    String POST_ANNOT = "postAnnotation";
    String DELETE_ANNOT = "deleteAnnotation";
    String UPDATE_ANNOT = "updateAnnotation";

    // 그룹, 페이지 공통
    String DELETE_PAGE = "deletePage";
    String PUT_PAGE_TITLE = "putPageTitle";
}
