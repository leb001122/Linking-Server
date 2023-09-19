package com.linking.user.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailedRes {

    private Long userId;
    private String lastName;
    private String firstName;
    private String email;

}
