package com.linking.push_notification.domain;

import com.linking.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationBadge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_Id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int unreadCount;

    public PushNotificationBadge(User user) {
        this.user = user;
    }

    public void increaseUnreadCount() {
        unreadCount++;
    }

    public void resetUnreadCount() {
        unreadCount = 0;
    }
}
