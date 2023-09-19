package com.linking.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmailReq {

    @NotNull
    private Long projectId;

    @NotNull
    private String partOfEmail;

}
