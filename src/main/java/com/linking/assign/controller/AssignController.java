package com.linking.assign.controller;

import com.linking.assign.service.AssignService;
import com.linking.assign.dto.AssignDeleteReq;
import com.linking.assign.dto.AssignRes;
import com.linking.assign.dto.AssignStatusUpdateReq;
import com.linking.global.common.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/assigns")
@RequiredArgsConstructor
public class AssignController {

    private final AssignService assignService;

    @GetMapping("/ratio/project/{id}")
    public ResponseEntity<Object> getAssignsCompletionRatio(@PathVariable Long id){
        return ResponseHandler.generateOkResponse(
                assignService.getAssignCompletionRate(id));
    }

    @PutMapping("/status")
    public ResponseEntity<Object> putAssignStatus(@RequestBody AssignStatusUpdateReq assignStatusUpdateReq){
        Optional<AssignRes> assignRes = assignService.updateAssignStatus(assignStatusUpdateReq);
        if(assignRes.isEmpty())
            return ResponseHandler.generateOkResponse(false);
        return ResponseHandler.generateOkResponse(true);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @PostMapping("/assigns/status/reset")
    public void resetAssignStatus(){
        assignService.setAssignStatus();
    }

    @PostMapping
    public ResponseEntity<Object> deleteAssign(@RequestBody AssignDeleteReq assignDeleteReq){
        assignService.deleteAssign(assignDeleteReq);
        return ResponseHandler.generateNoContentResponse();
    }

}
