package com.linking.global.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static final String MSG_200 = "요청 처리 완료";
    public static final String MSG_201 = "생성 완료";
    public static final String MSG_204 = "삭제 완료";
    public static final String MSG_400 = "잘못된 요청";
    public static final String MSG_404 = "대상 없음";
    public static final String MSG_500 = "서버 오류";

    public static ResponseEntity<Object> generateResponse(
            String message, HttpStatus status, Object resObj) {

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", resObj);

        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity generateOkResponse(Object resObj){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_200);
        map.put("status", HttpStatus.OK.value());
        map.put("data", resObj);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static ResponseEntity generateCreatedResponse(Object resObj){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_201);
        map.put("status", HttpStatus.CREATED.value());
        map.put("data", resObj);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    public static ResponseEntity<Object> generateNoContentResponse(){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_204);
        map.put("status", HttpStatus.NO_CONTENT.value());
        map.put("data", null);

        return new ResponseEntity<>(map, HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity generateBadRequestResponse(){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_400);
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("data", null);

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity generateNotFoundResponse(){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_404);
        map.put("status", HttpStatus.NOT_FOUND.value());
        map.put("data", null);

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity generateInternalServerErrorResponse(){
        Map<String, Object> map = new HashMap<>();
        map.put("message", MSG_500);
        map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("data", null);

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}