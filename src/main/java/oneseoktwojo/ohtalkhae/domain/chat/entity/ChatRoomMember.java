package oneseoktwojo.ohtalkhae.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oneseoktwojo.ohtalkhae.domain.chat.enums.Role;
import oneseoktwojo.ohtalkhae.domain.common.BaseTimeEntity;
import oneseoktwojo.ohtalkhae.domain.user.User;

import java.time.LocalDateTime;

/**
 * 채팅방 멤버 엔티티 클래스
 * 채팅방과 사용자 간의 관계 정보를 저장합니다.
 */
@Entity
@Table(name = "chat_room_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomMemberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    @Setter
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Builder.Default
    private boolean notificationEnabled = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id")
    private Message lastReadMessage;
    
    // BaseTimeEntity에서 createdAt(참여일), updatedAt(정보 수정일) 상속받음
    
    /**
     * 알림 설정 업데이트
     * @param enabled 알림 활성화 여부
     */
    public void updateNotificationSettings(boolean enabled) {
        this.notificationEnabled = enabled;
    }
    
    /**
     * 마지막으로 읽은 메시지 업데이트
     * @param message 마지막으로 읽은 메시지
     */
    public void updateLastReadMessage(Message message) {
        this.lastReadMessage = message;
    }
    
    /**
     * 채팅방 내 역할 변경
     * @param role 새 역할
     */
    public void updateRole(Role role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "ChatRoomMember{" +
                "id=" + chatRoomMemberId +
                ", chatRoomId=" + (chatRoom != null ? chatRoom.getChatRoomId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", role=" + role +
                '}';
    }
} 