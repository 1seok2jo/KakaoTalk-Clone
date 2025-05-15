package oneseoktwojo.ohtalkhae.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티 클래스
 * 채팅방의 기본 정보를 저장합니다.
 */
@Entity
@Table(name = "chat_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private ChatRoomType type;
    
    // BaseTimeEntity에서 createdAt, updatedAt 상속받음
    
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomMember> members = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    
    /**
     * 채팅방 이름 업데이트
     * @param name 새 채팅방 이름
     */
    public void updateName(String name) {
        this.name = name;
    }
    
    /**
     * 채팅방이 1:1 채팅인지 확인
     * @return 1:1 채팅 여부
     */
    public boolean isDirect() {
        return this.type == ChatRoomType.DIRECT;
    }
    
    /**
     * 채팅방이 그룹 채팅인지 확인
     * @return 그룹 채팅 여부
     */
    public boolean isGroup() {
        return this.type == ChatRoomType.GROUP;
    }
    
    /**
     * 채팅방에 멤버 추가 (양방향 관계 설정)
     * @param member 추가할 멤버
     */
    public void addMember(ChatRoomMember member) {
        this.members.add(member);
        member.setChatRoom(this);
    }

    /**
     * 채팅방에서 멤버 제거 (양방향 관계 해제)
     * @param member 제거할 멤버
     */
    public void removeMember(ChatRoomMember member) {
        this.members.remove(member);
        member.setChatRoom(null);
    }
    
    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + chatRoomId +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
} 