package oneseoktwojo.ohtalkhae.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {

    private Long chatRoomId;
    private String name;
    private ChatRoomType type;
    private LocalDateTime createdAt;
    // private int memberCount; // 필요시 추가

    @Builder
    public ChatRoomResponse(Long chatRoomId, String name, ChatRoomType type, LocalDateTime createdAt) {
        this.chatRoomId = chatRoomId;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
    }

    // ChatRoom 엔티티를 ChatRoomResponse DTO로 변환하는 정적 메소드
    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        if (chatRoom == null) {
            return null;
        }
        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .createdAt(chatRoom.getCreatedAt()) // BaseTimeEntity 상속 가정
                .build();
    }
} 