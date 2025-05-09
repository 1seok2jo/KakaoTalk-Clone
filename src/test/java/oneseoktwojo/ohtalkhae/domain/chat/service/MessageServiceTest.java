package oneseoktwojo.ohtalkhae.domain.chat.service;

import oneseoktwojo.ohtalkhae.domain.chat.dto.request.MessageRequest;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.Message;
import oneseoktwojo.ohtalkhae.domain.chat.entity.MessageRead;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomMemberRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.MessageReadRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.MessageRepository;
import oneseoktwojo.ohtalkhae.domain.user.User;
import oneseoktwojo.ohtalkhae.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageReadRepository messageReadRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @InjectMocks
    private MessageService messageService;

    // 테스트 공통 데이터
    private Long userId;
    private Long roomId;
    private User testUser;
    private ChatRoom testChatRoom;
    private Message testMessage;
    private MessageRequest testRequest;

    @BeforeEach
    void setUp() {
        // 기본 테스트 데이터 설정
        userId = 1L;
        roomId = 1L;

        testUser = User.builder()
                .userId(userId)
                .username("사용자1")
                .build();

        testChatRoom = ChatRoom.builder()
                .chatRoomId(roomId)
                .name("테스트 채팅방")
                .type(ChatRoomType.GROUP)
                .build();

        testMessage = Message.builder()
                .messageId(1L)
                .chatRoom(testChatRoom)
                .sender(testUser)
                .content("안녕하세요!")
                .type(MessageType.TEXT)
                .build();

        testRequest = MessageRequest.builder()
                .roomId(roomId)
                .content("안녕하세요!")
                .build();
    }

    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessage_Success() {
        // Given
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(testChatRoom));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(chatRoomMemberRepository.existsByChatRoomChatRoomIdAndUserUserId(roomId, userId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(chatRoomMemberRepository.countMembersByChatRoomId(roomId)).thenReturn(2);
        when(messageReadRepository.countByMessageMessageId(anyLong())).thenReturn(1);

        // When
        MessageDto result = messageService.sendMessage(testRequest, userId);

        // Then
        assertNotNull(result);
        assertEquals(testMessage.getMessageId(), result.getMessageId());
        assertEquals(testMessage.getContent(), result.getContent());
        assertEquals(testMessage.getSender().getUserId(), result.getSenderId());

        verify(messageRepository).save(any(Message.class));
        verify(messageReadRepository).save(any());
    }

    @Test
    @DisplayName("메시지 전송 실패 - 채팅방 멤버가 아닌 경우")
    void sendMessage_NotMember_ThrowsException() {
        // Given
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(testChatRoom));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(chatRoomMemberRepository.existsByChatRoomChatRoomIdAndUserUserId(roomId, userId)).thenReturn(false);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            messageService.sendMessage(testRequest, userId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("채팅방 멤버가 아닙니다") ||
                exception.getReason().contains("not a member"));
    }

    @Test
    @DisplayName("메시지 읽음 표시 성공 테스트")
    void markMessageAsRead_Success() {
        // Given
        Long messageId = 1L;
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(messageReadRepository.existsByMessageMessageIdAndUserUserId(messageId, userId)).thenReturn(false);

        // When
        messageService.markMessageAsRead(messageId, userId);

        // Then
        verify(messageReadRepository).save(argThat(messageRead ->
                messageRead.getMessage().getMessageId().equals(messageId) &&
                messageRead.getUser().getUserId().equals(userId)
        ));
    }

    @Test
    @DisplayName("메시지 읽음 표시 중복 시 저장 안함")
    void markMessageAsRead_AlreadyRead_DoesNotSave() {
        // Given
        Long messageId = 1L;
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(messageReadRepository.existsByMessageMessageIdAndUserUserId(messageId, userId)).thenReturn(true);

        // When
        messageService.markMessageAsRead(messageId, userId);

        // Then
        verify(messageReadRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 메시지 조회 성공 테스트")
    void getChatRoomMessages_Success() {
        // Given
        int limit = 30;

        List<Message> messages = Arrays.asList(
                Message.builder()
                        .messageId(1L)
                        .chatRoom(testChatRoom)
                        .sender(testUser)
                        .content("첫 번째 메시지")
                        .type(MessageType.TEXT)
                        .build(),
                Message.builder()
                        .messageId(2L)
                        .chatRoom(testChatRoom)
                        .sender(testUser)
                        .content("두 번째 메시지")
                        .type(MessageType.TEXT)
                        .build()
        );

        when(messageRepository.findByChatRoomIdOrderByCreatedAtDesc(eq(roomId), any(PageRequest.class)))
                .thenReturn(messages);
        when(chatRoomMemberRepository.countMembersByChatRoomId(roomId)).thenReturn(2);
        when(messageReadRepository.countByMessageMessageId(anyLong())).thenReturn(1);

        // When
        List<MessageDto> results = messageService.getChatRoomMessages(roomId, limit);

        // Then
        assertEquals(2, results.size());
        assertEquals(messages.get(0).getMessageId(), results.get(0).getMessageId());
        assertEquals(messages.get(0).getContent(), results.get(0).getContent());
        assertEquals(messages.get(1).getMessageId(), results.get(1).getMessageId());
        assertEquals(messages.get(1).getContent(), results.get(1).getContent());
        
        // unreadCount 대신 사용 (MessageDto에는 getReadCount 메서드가 없음)
        assertEquals(1, results.get(0).getUnreadCount());
        assertEquals(1, results.get(1).getUnreadCount());
    }

    @Test
    @DisplayName("채팅방 메시지 조회 - 한도 값 테스트")
    void getChatRoomMessages_WithDifferentLimits() {
        // Given
        int smallLimit = 5;
        int largeLimit = 100;

        when(chatRoomMemberRepository.countMembersByChatRoomId(roomId)).thenReturn(2);
        when(messageReadRepository.countByMessageMessageId(anyLong())).thenReturn(1);

        when(messageRepository.findByChatRoomIdOrderByCreatedAtDesc(eq(roomId), argThat(pageRequest ->
                pageRequest.getPageSize() == smallLimit && pageRequest.getPageNumber() == 0)))
                .thenReturn(Arrays.asList(testMessage));

        when(messageRepository.findByChatRoomIdOrderByCreatedAtDesc(eq(roomId), argThat(pageRequest ->
                pageRequest.getPageSize() == largeLimit && pageRequest.getPageNumber() == 0)))
                .thenReturn(Arrays.asList(testMessage));

        // When
        List<MessageDto> smallResults = messageService.getChatRoomMessages(roomId, smallLimit);
        List<MessageDto> largeResults = messageService.getChatRoomMessages(roomId, largeLimit);

        // Then
        assertEquals(1, smallResults.size());
        assertEquals(1, largeResults.size());

        // PageRequest가 올바른 limit로 생성되었는지 검증
        verify(messageRepository).findByChatRoomIdOrderByCreatedAtDesc(eq(roomId),
                argThat(pageRequest -> pageRequest.getPageSize() == smallLimit));
        verify(messageRepository).findByChatRoomIdOrderByCreatedAtDesc(eq(roomId),
                argThat(pageRequest -> pageRequest.getPageSize() == largeLimit));
    }

    @Test
    @DisplayName("메시지 삭제 성공 테스트")
    void deleteMessage_Success() {
        // Given
        Long messageId = 1L;
        Message message = Message.builder()
                .messageId(messageId)
                .chatRoom(testChatRoom)
                .sender(testUser)
                .content("삭제할 메시지")
                .type(MessageType.TEXT)
                .isDeleted(false)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // When
        messageService.deleteMessage(messageId, userId);

        // Then
        assertTrue(message.isDeleted());
        assertEquals("삭제된 메시지입니다.", message.getContent());
        verify(messageRepository).save(message);
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 다른 사용자의 메시지")
    void deleteMessage_NotOwner_ThrowsException() {
        // Given
        Long messageId = 1L;
        Long otherUserId = 2L;
        Message message = Message.builder()
                .messageId(messageId)
                .chatRoom(testChatRoom)
                .sender(testUser)  // 메시지 소유자는 userId=1
                .content("삭제할 메시지")
                .type(MessageType.TEXT)
                .isDeleted(false)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            messageService.deleteMessage(messageId, otherUserId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
    void deleteMessage_NotFound_ThrowsException() {
        // Given
        Long messageId = 999L;
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            messageService.deleteMessage(messageId, userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}