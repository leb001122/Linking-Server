package com.linking.project.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectCreateReq {

    @NotBlank
    private String projectName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @NotNull
    @Size(min = 1)
    private List<Long> partList;

}
