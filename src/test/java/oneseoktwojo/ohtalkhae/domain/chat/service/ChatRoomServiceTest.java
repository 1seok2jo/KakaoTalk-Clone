package oneseoktwojo.ohtalkhae.domain.chat.service;

import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomResponse;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoomMember;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.chat.enums.Role;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomMemberRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomRepository;
import oneseoktwojo.ohtalkhae.domain.user.User;
import oneseoktwojo.ohtalkhae.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;
    
    // 테스트 공통 데이터
    private Long creatorId;
    private List<Long> memberIds;
    private User creator;
    private User member1;
    private User member2;
    private ChatRoom testChatRoom;
    private CreateChatRoomRequest testRequest;
    
    @BeforeEach
    void setUp() {
        // 기본 테스트 데이터 설정
        creatorId = 1L;
        memberIds = Arrays.asList(2L, 3L);
        
        creator = User.builder()
                .userId(creatorId)
                .username("사용자1")
                .build();
        
        member1 = User.builder()
                .userId(2L)
                .username("사용자2")
                .build();
        
        member2 = User.builder()
                .userId(3L)
                .username("사용자3")
                .build();
        
        testChatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .name("테스트 채팅방")
                .type(ChatRoomType.GROUP)
                .build();
        
        testRequest = CreateChatRoomRequest.builder()
                .name("테스트 그룹 채팅방")
                .type(ChatRoomType.GROUP)
                .memberIds(memberIds)
                .build();
    }

    @Test
    @DisplayName("1:1 채팅방 생성 성공 테스트")
    void createDirectChatRoom_Success() {
        // Given
        Long memberId = 2L;
        
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
                .type(ChatRoomType.DIRECT)
                .memberIds(List.of(memberId))
                .name("사용자1, 사용자2") // 1:1 채팅방은 이름이 자동으로 생성되지만 테스트에서는 필요
                .build();
        
        ChatRoom directChatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .name("사용자1, 사용자2")
                .type(ChatRoomType.DIRECT)
                .build();
        
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(creator, member1));
        when(chatRoomRepository.findDirectChatRoomByMemberIds(anyList())).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(directChatRoom);
        
        // When
        ChatRoomResponse response = chatRoomService.createChatRoom(request, creatorId);
        
        // Then
        assertNotNull(response);
        assertEquals(directChatRoom.getChatRoomId(), response.getChatRoomId());
        assertEquals(directChatRoom.getName(), response.getName());
        assertEquals(directChatRoom.getType(), response.getType());
        assertEquals(ChatRoomType.DIRECT, response.getType());
        
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(chatRoomMemberRepository).saveAll(argThat(members -> {
            // 1:1 채팅방은 두 명의 멤버를 가져야 함
            return ((List<ChatRoomMember>)members).size() == 2;
        }));
    }
    
    @Test
    @DisplayName("1:1 채팅방 중복 생성 시 기존 채팅방 반환")
    void createDirectChatRoom_ExistingRoom_Success() {
        // Given
        Long memberId = 2L;
        
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
                .type(ChatRoomType.DIRECT)
                .memberIds(List.of(memberId))
                .name("사용자1, 사용자2") // 1:1 채팅방은 이름이 자동으로 생성되지만 테스트에서는 필요
                .build();
        
        ChatRoom existingDirectChatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .name("사용자1, 사용자2")
                .type(ChatRoomType.DIRECT)
                .build();
        
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(creator, member1));
        when(chatRoomRepository.findDirectChatRoomByMemberIds(anyList())).thenReturn(Optional.of(existingDirectChatRoom));
        
        // When
        ChatRoomResponse response = chatRoomService.createChatRoom(request, creatorId);
        
        // Then
        assertNotNull(response);
        assertEquals(existingDirectChatRoom.getChatRoomId(), response.getChatRoomId());
        
        // 기존 채팅방을 반환했으므로 저장 작업이 없어야 함
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        verify(chatRoomMemberRepository, never()).saveAll(anyList());
    }
    
    @Test
    @DisplayName("그룹 채팅방 생성 성공 테스트")
    void createGroupChatRoom_Success() {
        // Given
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(creator, member1, member2));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(testChatRoom);
        
        // When
        ChatRoomResponse response = chatRoomService.createChatRoom(testRequest, creatorId);
        
        // Then
        assertNotNull(response);
        assertEquals(testChatRoom.getChatRoomId(), response.getChatRoomId());
        assertEquals(testChatRoom.getName(), response.getName());
        assertEquals(testChatRoom.getType(), response.getType());
        assertEquals(ChatRoomType.GROUP, response.getType());
        
        verify(chatRoomRepository).save(argThat(chatRoom -> 
            chatRoom.getName().equals(testRequest.getName()) && 
            chatRoom.getType() == ChatRoomType.GROUP
        ));
        verify(chatRoomMemberRepository).saveAll(argThat(members -> {
            // 생성자를 포함한 멤버 수 검증
            return ((List<ChatRoomMember>)members).size() == memberIds.size() + 1;
        }));
    }
    
    @Test
    @DisplayName("채팅방 생성 실패 - 멤버 없음")
    void createChatRoom_NoMembers_ThrowsException() {
        // Given
        CreateChatRoomRequest emptyMembersRequest = CreateChatRoomRequest.builder()
                .name("테스트 채팅방")
                .type(ChatRoomType.GROUP)
                .memberIds(new ArrayList<>())
                .build();
        
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            chatRoomService.createChatRoom(emptyMembersRequest, creatorId);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("멤버") || 
                  exception.getReason().contains("member"));
        
        verify(chatRoomRepository, never()).save(any());
        verify(chatRoomMemberRepository, never()).saveAll(any());
    }
    
    @Test
    @DisplayName("채팅방 이름 업데이트 성공 테스트")
    void updateChatRoomName_Success() {
        // Given
        Long roomId = 1L;
        String newName = "새로운 채팅방 이름";
        
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(roomId)
                .name("기존 채팅방 이름")
                .type(ChatRoomType.GROUP)
                .build();
        
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        
        // When
        chatRoomService.updateChatRoomName(roomId, newName);
        
        // Then
        assertEquals(newName, chatRoom.getName());
        verify(chatRoomRepository).save(chatRoom);
    }
    
    @Test
    @DisplayName("1:1 채팅방 이름 변경 시 예외 발생 테스트")
    void updateDirectChatRoomName_ThrowsException() {
        // Given
        Long roomId = 1L;
        String newName = "새로운 채팅방 이름";
        
        ChatRoom directChatRoom = ChatRoom.builder()
                .chatRoomId(roomId)
                .name("사용자1, 사용자2")
                .type(ChatRoomType.DIRECT)
                .build();
        
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(directChatRoom));
        
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            chatRoomService.updateChatRoomName(roomId, newName);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("1:1 채팅방") || 
                  exception.getReason().contains("direct chat room"));
        
        // 이름이 변경되지 않았는지 확인
        assertEquals("사용자1, 사용자2", directChatRoom.getName());
        verify(chatRoomRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("채팅방 이름 업데이트 실패 - 존재하지 않는 채팅방")
    void updateChatRoomName_NotFound_ThrowsException() {
        // Given
        Long roomId = 999L;
        String newName = "새로운 채팅방 이름";
        
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            chatRoomService.updateChatRoomName(roomId, newName);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
    
    @Test
    @DisplayName("채팅방 나가기 성공 테스트 - 마지막 멤버")
    void leaveChatRoom_LastMember_Success() {
        // Given
        Long roomId = 1L;
        Long userId = 1L;
        
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoomMemberId(1L)
                .chatRoom(testChatRoom)
                .user(creator)
                .role(Role.OWNER)
                .build();
        
        when(chatRoomMemberRepository.findByChatRoomChatRoomIdAndUserUserId(roomId, userId))
                .thenReturn(Optional.of(member));
        when(chatRoomMemberRepository.countMembersByChatRoomId(roomId)).thenReturn(1);
        
        // When
        chatRoomService.leaveChatRoom(roomId, userId);
        
        // Then
        verify(chatRoomMemberRepository).delete(member);
        verify(chatRoomRepository).delete(testChatRoom);
    }
    
    @Test
    @DisplayName("채팅방 나가기 성공 테스트 - 일반 멤버")
    void leaveChatRoom_RegularMember_Success() {
        // Given
        Long roomId = 1L;
        Long userId = 2L; // 일반 멤버
        
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoomMemberId(2L)
                .chatRoom(testChatRoom)
                .user(member1) // userId = 2L
                .role(Role.MEMBER)
                .build();
        
        when(chatRoomMemberRepository.findByChatRoomChatRoomIdAndUserUserId(roomId, userId))
                .thenReturn(Optional.of(member));
        when(chatRoomMemberRepository.countMembersByChatRoomId(roomId)).thenReturn(3);
        
        // When
        chatRoomService.leaveChatRoom(roomId, userId);
        
        // Then
        verify(chatRoomMemberRepository).delete(member);
        verify(chatRoomRepository, never()).delete(any()); // 채팅방은 삭제되지 않음
    }
    
    @Test
    @DisplayName("채팅방 나가기 실패 - 채팅방 멤버가 아님")
    void leaveChatRoom_NotMember_ThrowsException() {
        // Given
        Long roomId = 1L;
        Long userId = 999L; // 멤버가 아닌 사용자
        
        when(chatRoomMemberRepository.findByChatRoomChatRoomIdAndUserUserId(roomId, userId))
                .thenReturn(Optional.empty());
        
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            chatRoomService.leaveChatRoom(roomId, userId);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(chatRoomMemberRepository, never()).delete(any());
        verify(chatRoomRepository, never()).delete(any());
    }
}