package com.linking.chatroom_badge.domain;

import com.linking.chatroom.domain.ChatRoom;
import com.linking.participant.domain.Participant;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chatroom_badge")
public class ChatRoomBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long badgeId;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "unread_count")
    private int unreadCount;

    public void plusCount(){
       unreadCount++;
    }

    public void resetCnt(){
        unreadCount = 0;
    }

}
