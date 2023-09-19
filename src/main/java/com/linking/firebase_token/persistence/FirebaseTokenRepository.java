package com.linking.firebase_token.persistence;

import com.linking.firebase_token.domain.FirebaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {


    @Query("SELECT f FROM FirebaseToken f WHERE f.user.userId = :userId")
    Optional<FirebaseToken> findByUserId(@Param("userId") Long userId);
}
