package com.linking.push_settings.domain;

import com.linking.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushSettings {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_settings_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean allowedAppPush;
    private boolean allowedWebPush;

    private boolean allowedMail;

    @Builder
    public PushSettings(User user) {
        this.user = user;
    }

    public void setAppSettings(boolean allowedAppPush, boolean allowedMail) {
        this.allowedAppPush = allowedAppPush;
        this.allowedMail = allowedMail;
    }

    public void setWepSettings(boolean allowedWebPush, boolean allowedMail) {
        this.allowedWebPush = allowedWebPush;
        this.allowedMail = allowedMail;
    }

    @PrePersist
    public void prePersist() {
        this.allowedMail = true;
    }
}
