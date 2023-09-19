package com.linking.chatroom_badge.persistence;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chatroom_badge.domain.ChatRoomBadge;
import com.linking.participant.domain.Participant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomBadgeRepository extends JpaRepository<ChatRoomBadge, Long> {

    Optional<ChatRoomBadge> findChatRoomBadgeByParticipant(@Param("participant") Participant participant);

    @EntityGraph(attributePaths = {"participant"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ChatRoomBadge> findChatRoomBadgesByChatRoom(@Param("chatroom") ChatRoom chatRoom);


}
