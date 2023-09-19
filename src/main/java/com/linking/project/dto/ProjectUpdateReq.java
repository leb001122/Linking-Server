package com.linking.project.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectUpdateReq {

    @NotNull
    private Long projectId;

    @NotNull
    private String projectName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private List<Long> partList;

    private Boolean isPartListChanged;

}
