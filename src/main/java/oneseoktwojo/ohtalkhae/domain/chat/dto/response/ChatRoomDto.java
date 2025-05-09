package oneseoktwojo.ohtalkhae.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long chatRoomId;
    private String name;
    private ChatRoomType type;
    private List<ChatRoomMemberDto> members;
    private Integer unreadCount;
    private MessageDto lastMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean notificationEnabled;
} 