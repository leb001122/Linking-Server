package com.linking.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignUpReq {

    @NotNull
    private String lastName;

    @NotNull
    private String firstName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
