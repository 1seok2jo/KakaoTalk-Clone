package oneseoktwojo.ohtalkhae.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 메시지 엔티티 클래스
 * 채팅방 내에서 주고받는 메시지 정보를 저장합니다.
 */
@Entity
@Table(name = "message")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @Column(length = 50000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private MessageType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private Message replyToMessage;
    
    @Builder.Default
    private boolean isDeleted = false;
    
    @Builder.Default
    private boolean isEdited = false;
    
    @ElementCollection
    @CollectionTable(name = "message_mentioned_users", 
                    joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "user_id")
    @Builder.Default
    private List<Long> mentionedUserIds = new ArrayList<>();
    
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageRead> reads = new ArrayList<>();
    
    /**
     * 메시지 내용 수정
     * @param content 새 메시지 내용
     */
    public void updateContent(String content) {
        this.content = content;
        this.isEdited = true;
    }
    
    /**
     * 메시지 삭제 처리 (소프트 딜리트)
     * 실제로 삭제하지 않고 삭제 표시만 합니다.
     */
    public void markAsDeleted() {
        this.isDeleted = true;
        this.content = "삭제된 메시지입니다.";
    }
    
    /**
     * 메시지가 특정 사용자에 의해 편집/삭제 가능한지 확인
     * @param userId 사용자 ID
     * @return 편집/삭제 가능 여부
     */
    public boolean isEditableBy(Long userId) {
        return sender.getUserId().equals(userId) && !isDeleted;
    }
    
    /**
     * 멘션된 사용자 추가
     * @param userId 멘션할 사용자 ID
     */
    public void addMentionedUser(Long userId) {
        if (mentionedUserIds == null) {
            mentionedUserIds = new ArrayList<>();
        }
        if (!mentionedUserIds.contains(userId)) {
            mentionedUserIds.add(userId);
        }
    }
    
    /**
     * 채팅방 연관관계 설정 (양방향 관계)
     * @param chatRoom 채팅방
     */
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    /**
     * 메시지 읽음 정보 추가 (양방향 관계)
     * @param read 메시지 읽음 정보
     */
    public void addMessageRead(MessageRead read) {
        this.reads.add(read);
        read.setMessage(this);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + messageId +
                ", chatRoomId=" + (chatRoom != null ? chatRoom.getChatRoomId() : null) +
                ", senderId=" + (sender != null ? sender.getUserId() : null) +
                ", type=" + type +
                ", isDeleted=" + isDeleted +
                ", isEdited=" + isEdited +
                '}';
    }
} 