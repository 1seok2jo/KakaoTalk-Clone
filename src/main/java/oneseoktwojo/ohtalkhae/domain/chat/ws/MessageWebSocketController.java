package oneseoktwojo.ohtalkhae.domain.chat.ws;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.MessageRequest;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import oneseoktwojo.ohtalkhae.domain.chat.service.MessageService;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 웹소켓 기반 실시간 채팅 메시지 컨트롤러
 * STOMP 프로토콜을 사용하여 클라이언트와 서버 간 실시간 메시지 통신을 처리합니다.
 * 주요 기능: 메시지 송수신, 채팅방 입장/퇴장 알림, 타이핑 상태 알림
 */
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 메시지 전송 처리
     * 클라이언트는 /app/chat.sendMessage로 메시지를 보내고
     * 서버는 /topic/chatroom/{roomId}로 메시지를 브로드캐스트합니다.
     * 
     * @param request 클라이언트로부터 받은 메시지 요청 객체
     * @param user 현재 인증된 사용자 정보
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest request, @AuthenticationPrincipal User user) {
        MessageDto messageDto = messageService.sendMessage(request, user.getUserId());
        messagingTemplate.convertAndSend("/topic/chatroom/" + request.getRoomId(), messageDto);
    }
    
    /**
     * 채팅방 입장 알림 처리
     * 클라이언트는 /app/chat.join/{roomId}로 메시지를 보내고
     * 서버는 해당 채팅방에 사용자 입장을 알립니다.
     * 
     * @param roomId 입장할 채팅방 ID
     * @param user 현재 인증된 사용자 정보
     */
    @MessageMapping("/chat.join/{roomId}")
    public void joinChatRoom(@DestinationVariable Long roomId, @AuthenticationPrincipal User user) {
        // 시스템 메시지 생성 또는 입장 처리 로직
        String message = user.getUsername() + "님이 입장했습니다.";
        messagingTemplate.convertAndSend("/topic/chatroom/" + roomId, 
            createSystemMessage(roomId, message));
    }
    
    /**
     * 채팅방 퇴장 알림 처리
     * 클라이언트는 /app/chat.leave/{roomId}로 메시지를 보내고
     * 서버는 해당 채팅방에 사용자 퇴장을 알립니다.
     * 
     * @param roomId 퇴장할 채팅방 ID
     * @param user 현재 인증된 사용자 정보
     */
    @MessageMapping("/chat.leave/{roomId}")
    public void leaveChatRoom(@DestinationVariable Long roomId, @AuthenticationPrincipal User user) {
        // 시스템 메시지 생성 또는 퇴장 처리 로직
        String message = user.getUsername() + "님이 퇴장했습니다.";
        messagingTemplate.convertAndSend("/topic/chatroom/" + roomId, 
            createSystemMessage(roomId, message));
    }
    
    /**
     * 타이핑 중 상태 알림 처리
     * 사용자가 메시지를 입력 중일 때 다른 사용자들에게 알립니다.
     * 
     * @param roomId 채팅방 ID
     * @param isTyping 타이핑 중 여부(true: 타이핑 중, false: 타이핑 중지)
     * @param user 현재 인증된 사용자 정보
     */
    @MessageMapping("/chat.typing/{roomId}")
    public void typingNotification(
            @DestinationVariable Long roomId, 
            @Payload boolean isTyping,
            @AuthenticationPrincipal User user) {
        // 타이핑 중 상태 전달
        TypingNotification notification = new TypingNotification(user.getUserId(), user.getUsername(), isTyping);
        messagingTemplate.convertAndSend("/topic/chatroom/" + roomId + "/typing", notification);
    }
    
    /**
     * 시스템 메시지 생성 헬퍼 메서드
     * 입장, 퇴장 등 시스템 알림을 위한 메시지 객체를 생성합니다.
     * 
     * @param roomId 채팅방 ID
     * @param content 시스템 메시지 내용
     * @return 생성된 시스템 메시지 DTO
     */
    private MessageDto createSystemMessage(Long roomId, String content) {
        return MessageDto.builder()
            .chatRoomId(roomId)
            .content(content)
            .type(MessageType.SYSTEM)
            .build();
    }
    
    /**
     * 타이핑 알림을 위한 내부 클래스
     * 사용자의 타이핑 상태 정보를 전달하기 위한 데이터 구조
     */
    @Getter
    @AllArgsConstructor
    private static class TypingNotification {
        private Long userId;      // 타이핑 중인 사용자 ID
        private String username;  // 타이핑 중인 사용자 이름
        private boolean typing;   // 타이핑 중 여부
    }
}

