package com.linking.page_check.persistence;

import com.linking.page_check.domain.PageCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageCheckRepository extends JpaRepository<PageCheck, Long> {

    @Query("SELECT p FROM PageCheck p INNER JOIN Participant t " +
            "ON (p.participant.participantId = t.participantId)" +
            "WHERE t.user.userId = :userId AND t.project.projectId = :projectId")
    List<PageCheck> findAllByParticipant(@Param("userId") Long userId, @Param("projectId") Long projectId);

//    @Query("SELECT p FROM PageCheck p JOIN p.participant WHERE p.participant.participantId = :participantId")
//    List<PageCheck> findAllByParticipantId(@Param("participantId") Long participantId);

    @Query("SELECT p FROM PageCheck p WHERE p.page.id = :pageId AND p.participant.participantId = :partId")
    Optional<PageCheck> findByPageAndPartId(@Param("pageId") Long pageId, @Param("partId") Long partId);

    @Query("SELECT p FROM PageCheck p WHERE p.page.id = :pageId")
    List<PageCheck> findAllByPageId(@Param("pageId") Long pageId);
}
