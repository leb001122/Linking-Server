package com.linking.annotation.controller;

import com.linking.annotation.dto.AnnotationRes;
import com.linking.annotation.dto.AnnotationUpdateReq;
import com.linking.annotation.service.AnnotationService;
import com.linking.annotation.dto.AnnotationCreateReq;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/annotations")
@RequiredArgsConstructor
public class AnnotationController {

    private final AnnotationService annotationService;

    @PostMapping
    public ResponseEntity<Object> postAnnotation(
            @RequestBody @Valid AnnotationCreateReq req,
            @RequestHeader Long userId
    ) {

        AnnotationRes res = annotationService.createAnnotation(req, userId);

        return ResponseHandler.generateCreatedResponse(res);
    }

    @PutMapping
    public ResponseEntity<Object> putAnnotation(
            @RequestBody @Valid AnnotationUpdateReq req,
            @RequestHeader Long userId
    ) {

        AnnotationRes annotationRes = annotationService.updateAnnotation(req, userId);
        return ResponseHandler.generateResponse(ResponseHandler.MSG_200, HttpStatus.OK, annotationRes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnnotation(
            @RequestHeader(value = "projectId") Long projectId,
            @PathVariable("id") Long id,
            @RequestHeader Long userId
    ) {

        annotationService.deleteAnnotation(id, projectId, userId);

        return ResponseHandler.generateNoContentResponse();
    }
}
