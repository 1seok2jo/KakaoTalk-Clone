package oneseoktwojo.ohtalkhae.domain.chat.mapper;

import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.Message;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomMemberDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 관련 엔티티를 DTO로 변환하는 매퍼 클래스
 */
public class ChatMapper {

    /**
     * Message 엔티티를 MessageDto로 변환
     * 
     * @param message 메시지 엔티티
     * @param unreadCount 읽지 않은 수
     * @return 메시지 DTO
     */
    public static MessageDto toMessageDto(Message message, int unreadCount) {
        if (message == null) {
            return null;
        }
        
        // 답장 대상 메시지가 있는 경우 미리보기 DTO 생성
        MessageDto.MessagePreviewDto replyPreview = null;
        if (message.getReplyToMessage() != null) {
            Message replyTo = message.getReplyToMessage();
            String previewContent = replyTo.getContent();
            if (previewContent.length() > 30) {
                previewContent = previewContent.substring(0, 30) + "...";
            }
            
            replyPreview = MessageDto.MessagePreviewDto.builder()
                .messageId(replyTo.getMessageId())
                .senderId(replyTo.getSender().getUserId())
                .senderNickname(replyTo.getSender().getUsername())
                .previewContent(previewContent)
                .build();
        }
        
        return MessageDto.builder()
            .messageId(message.getMessageId())
            .chatRoomId(message.getChatRoom().getChatRoomId())
            .senderId(message.getSender().getUserId())
            .senderNickname(message.getSender().getUsername())
            .senderProfileUrl(message.getSender().getProfileImageUrl())
            .content(message.getContent())
            .type(message.getType())
            .isDeleted(message.isDeleted())
            .isEdited(message.isEdited())
            .replyToMessageId(message.getReplyToMessage() != null ? message.getReplyToMessage().getMessageId() : null)
            .replyToMessage(replyPreview)
            .mentionedUserIds(message.getMentionedUserIds())
            .unreadCount(unreadCount)
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }
    
    /**
     * ChatRoom 엔티티를 ChatRoomDto로 변환
     * 
     * @param chatRoom 채팅방 엔티티
     * @param currentUserId 현재 사용자 ID
     * @return 채팅방 DTO
     */
    public static ChatRoomDto toChatRoomDto(ChatRoom chatRoom, Long currentUserId) {
        if (chatRoom == null) {
            return null;
        }
        
        // 채팅방 멤버 목록 (현 단계에서는 간소화)
        List<ChatRoomMemberDto> members = List.of(
            ChatRoomMemberDto.builder()
                .userId(currentUserId)
                .username("현재 사용자")
                .build()
        );
        
        // 마지막 메시지 (현 단계에서는 null)
        MessageDto lastMessage = null;
        
        return ChatRoomDto.builder()
            .chatRoomId(chatRoom.getChatRoomId())
            .name(chatRoom.getName())
            .type(chatRoom.getType())
            .members(members)
            .unreadCount(0) // 실제로는 계산 필요
            .lastMessage(lastMessage)
            .createdAt(chatRoom.getCreatedAt())
            .updatedAt(chatRoom.getUpdatedAt())
            .notificationEnabled(true) // 기본값
            .build();
    }
}
