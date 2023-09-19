package com.linking.firebase_token.domain;

import com.linking.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirebaseToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String appToken;
    private Timestamp appTimestamp;
    private String webToken;
    private Timestamp webTimestamp;

    @Builder
    public FirebaseToken(User user) {
        this.user = user;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
        this.appTimestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    public void setWebToken(String webToken) {
        this.webToken = webToken;
        this.webTimestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
