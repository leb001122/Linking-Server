package com.linking.chat.persistence;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.chat.domain.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @EntityGraph(attributePaths = {"participant"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT c FROM Chat c WHERE c.chatroom = :chatRoom ORDER by c.sentDatetime DESC")
    Page<Chat> findMessagesByChatroom(ChatRoom chatRoom, Pageable pageable);

}