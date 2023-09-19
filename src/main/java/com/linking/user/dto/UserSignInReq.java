package com.linking.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignInReq {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

}
