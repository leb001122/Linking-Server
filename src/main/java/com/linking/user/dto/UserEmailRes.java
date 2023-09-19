package com.linking.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmailRes {

    private Boolean emailExists;
    private List<UserDetailedRes> userList;

}
