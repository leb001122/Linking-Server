package com.linking.push_notification.persistence;

import com.linking.push_notification.domain.PushNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {


    @Query("SELECT n FROM PushNotification n WHERE n.user.userId = :userId order by n.id desc")
    List<PushNotification> findAllByUserId(@Param("userId") Long userId);
}
