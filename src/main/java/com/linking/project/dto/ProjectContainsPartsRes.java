package com.linking.project.dto;

import com.linking.user.dto.UserDetailedRes;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectContainsPartsRes {

    private Long projectId;
    private String projectName;
    private LocalDate beginDate;
    private LocalDate dueDate;
    private Long ownerId;
    private List<UserDetailedRes> partList;

}
