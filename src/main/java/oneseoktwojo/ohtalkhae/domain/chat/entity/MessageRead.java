package oneseoktwojo.ohtalkhae.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oneseoktwojo.ohtalkhae.domain.user.User;

import java.time.LocalDateTime;

/**
 * 메시지 읽음 정보 엔티티 클래스
 * 사용자별 메시지 읽음 상태를 저장합니다.
 */
@Entity
@Table(name = "message_read")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageReadId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    @Setter
    private Message message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDateTime readAt;
    
    /**
     * 생성 시 읽은 시간 초기화
     * JPA의 @PrePersist와 함께 사용
     */
    @PrePersist
    public void initReadAt() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "MessageRead{" +
                "id=" + messageReadId +
                ", messageId=" + (message != null ? message.getMessageId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", readAt=" + readAt +
                '}';
    }
} 