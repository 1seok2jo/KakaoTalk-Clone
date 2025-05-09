package oneseoktwojo.ohtalkhae.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileUrl;
    private String content;
    private MessageType type;
    private boolean isDeleted;
    private boolean isEdited;
    private Long replyToMessageId;
    private MessagePreviewDto replyToMessage;
    private List<Long> mentionedUserIds;
    private int unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagePreviewDto {
        private Long messageId;
        private Long senderId;
        private String senderNickname;
        private String previewContent;
    }
} 