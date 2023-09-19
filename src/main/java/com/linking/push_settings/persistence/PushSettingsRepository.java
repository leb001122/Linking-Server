package com.linking.push_settings.persistence;

import com.linking.push_settings.domain.PushSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushSettingsRepository extends JpaRepository<PushSettings, Long> {


    @Query("SELECT s FROM PushSettings s WHERE s.user.userId = :userId")
    Optional<PushSettings> findByUserId(@Param("userId") Long userId);
}
