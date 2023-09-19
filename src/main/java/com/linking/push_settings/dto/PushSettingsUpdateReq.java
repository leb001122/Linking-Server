package com.linking.push_settings.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushSettingsUpdateReq {

    @NotNull
    private Long userId;
    @NotNull
    private boolean allowedWebAppPush;
    @NotNull
    private boolean allowedMail;
}
