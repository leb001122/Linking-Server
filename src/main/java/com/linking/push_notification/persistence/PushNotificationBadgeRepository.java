package com.linking.push_notification.persistence;

import com.linking.push_notification.domain.PushNotificationBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushNotificationBadgeRepository  extends JpaRepository<PushNotificationBadge, Long> {

    @Query("SELECT b.unreadCount FROM PushNotificationBadge b WHERE b.user.userId = :userId")
    int findBadgeCountByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM PushNotificationBadge b WHERE b.user.userId = :userId")
    PushNotificationBadge findByUserId(@Param("userId") Long userId);
}
