package oneseoktwojo.ohtalkhae.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomResponse;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.chat.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatRoomService chatRoomService;
    
    // 테스트 공통 데이터
    private Long testUserId;
    private User testUserPrincipal;
    private CreateChatRoomRequest createChatRoomRequest;
    private ChatRoomResponse chatRoomResponse;
    private List<ChatRoomDto> chatRoomsData;
    
    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testUserPrincipal = new User("testuser", "password", 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        // 채팅방 생성 요청 데이터
        createChatRoomRequest = CreateChatRoomRequest.builder()
                .name("테스트 채팅방")
                .type(ChatRoomType.GROUP)
                .memberIds(Arrays.asList(2L, 3L))
                .build();
        
        // 채팅방 응답 데이터
        LocalDateTime now = LocalDateTime.now();
        chatRoomResponse = ChatRoomResponse.builder()
                .chatRoomId(1L)
                .name("테스트 채팅방")
                .type(ChatRoomType.GROUP)
                .createdAt(now)
                .build();
        
        // 채팅방 목록 데이터
        chatRoomsData = Arrays.asList(
            createChatRoomDto(1L, "채팅방 1", ChatRoomType.GROUP),
            createChatRoomDto(2L, "채팅방 2", ChatRoomType.DIRECT)
        );
    }

    @Test
    @DisplayName("채팅방 생성 API 테스트")
    void createChatRoom() throws Exception {
        // Given
        when(chatRoomService.createChatRoom(any(CreateChatRoomRequest.class), eq(testUserId)))
                .thenReturn(chatRoomResponse);

        // When
        ResultActions result = mockMvc.perform(post("/api/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createChatRoomRequest))
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
                
        // Then
        result.andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.chatRoomId").value(chatRoomResponse.getChatRoomId()))
              .andExpect(jsonPath("$.name").value(chatRoomResponse.getName()))
              .andExpect(jsonPath("$.type").value(chatRoomResponse.getType().toString()));
        
        // 서비스 호출 파라미터 검증
        ArgumentCaptor<CreateChatRoomRequest> requestCaptor = ArgumentCaptor.forClass(CreateChatRoomRequest.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(chatRoomService).createChatRoom(requestCaptor.capture(), userIdCaptor.capture());
        
        CreateChatRoomRequest capturedRequest = requestCaptor.getValue();
        assertEquals(createChatRoomRequest.getName(), capturedRequest.getName());
        assertEquals(createChatRoomRequest.getType(), capturedRequest.getType());
        assertEquals(createChatRoomRequest.getMemberIds(), capturedRequest.getMemberIds());
        assertEquals(testUserId, userIdCaptor.getValue());
    }
    
    @Test
    @DisplayName("채팅방 생성 API 실패 - 부적절한 요청")
    void createChatRoom_BadRequest() throws Exception {
        // Given
        CreateChatRoomRequest invalidRequest = CreateChatRoomRequest.builder()
                .name("")
                .memberIds(Collections.emptyList())
                .build(); // type은 null
        
        // When & Then
        mockMvc.perform(post("/api/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
                
        verify(chatRoomService, never()).createChatRoom(any(), anyLong());
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 API 테스트")
    void getUserChatRooms() throws Exception {
        // Given
        when(chatRoomService.getUserChatRooms(eq(testUserId))).thenReturn(chatRoomsData);

        // When
        ResultActions result = mockMvc.perform(get("/api/chatrooms")
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal)));
                
        // Then
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$[0].chatRoomId").value(chatRoomsData.get(0).getChatRoomId()))
              .andExpect(jsonPath("$[0].name").value(chatRoomsData.get(0).getName()))
              .andExpect(jsonPath("$[1].chatRoomId").value(chatRoomsData.get(1).getChatRoomId()))
              .andExpect(jsonPath("$[1].name").value(chatRoomsData.get(1).getName()));
              
        // 서비스 호출 검증
        verify(chatRoomService).getUserChatRooms(eq(testUserId));
    }
    
    @Test
    @DisplayName("사용자 채팅방 목록 조회 API - 비어있는 경우")
    void getUserChatRooms_EmptyList() throws Exception {
        // Given
        when(chatRoomService.getUserChatRooms(eq(testUserId))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/chatrooms")
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("채팅방 이름 업데이트 API 테스트")
    void updateChatRoomName() throws Exception {
        // Given
        Long roomId = 1L;
        String newName = "새로운 채팅방 이름";

        // When
        ResultActions result = mockMvc.perform(patch("/api/chatrooms/{roomId}", roomId)
                .param("newName", newName)
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
                
        // Then
        result.andExpect(status().isOk());
        verify(chatRoomService).updateChatRoomName(eq(roomId), eq(newName));
    }
    
    @Test
    @DisplayName("채팅방 이름 업데이트 API 실패 - 채팅방 없음")
    void updateChatRoomName_NotFound() throws Exception {
        // Given
        Long nonExistingRoomId = 999L;
        String newName = "새로운 채팅방 이름";
        
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."))
            .when(chatRoomService).updateChatRoomName(eq(nonExistingRoomId), any());

        // When & Then
        mockMvc.perform(patch("/api/chatrooms/{roomId}", nonExistingRoomId)
                .param("newName", newName)
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채팅방 나가기 API 테스트")
    void leaveChatRoom() throws Exception {
        // Given
        Long roomId = 1L;

        // When
        ResultActions result = mockMvc.perform(delete("/api/chatrooms/{roomId}/members/me", roomId)
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
                
        // Then
        result.andExpect(status().isNoContent());
        verify(chatRoomService).leaveChatRoom(eq(roomId), eq(testUserId));
    }
    
    @Test
    @DisplayName("채팅방 나가기 API 실패 - 멤버가 아님")
    void leaveChatRoom_NotMember() throws Exception {
        // Given
        Long roomId = 1L;
        
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "채팅방 멤버가 아닙니다."))
            .when(chatRoomService).leaveChatRoom(eq(roomId), eq(testUserId));

        // When & Then
        mockMvc.perform(delete("/api/chatrooms/{roomId}/members/me", roomId)
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("채팅방 알림 설정 업데이트 API 테스트")
    void updateNotificationSettings() throws Exception {
        // Given
        Long roomId = 1L;
        boolean notificationEnabled = false;

        // When
        ResultActions result = mockMvc.perform(put("/api/chatrooms/{roomId}/members/me/notification", roomId)
                .param("notificationEnabled", String.valueOf(notificationEnabled))
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()));
                
        // Then
        result.andExpect(status().isOk());
        verify(chatRoomService).updateNotificationSettings(eq(roomId), eq(testUserId), eq(notificationEnabled));
    }
    
    @Test
    @DisplayName("채팅방 알림 설정 업데이트 API - 매개변수 누락")
    void updateNotificationSettings_MissingParam() throws Exception {
        // Given
        Long roomId = 1L;
        // notificationEnabled 인자 누락

        // When & Then
        mockMvc.perform(put("/api/chatrooms/{roomId}/members/me/notification", roomId)
                .with(SecurityMockMvcRequestPostProcessors.user(testUserPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
                
        verify(chatRoomService, never()).updateNotificationSettings(anyLong(), anyLong(), anyBoolean());
    }

    /**
     * 채팅방 DTO 리턴
     */
    private ChatRoomDto createChatRoomDto(Long id, String name, ChatRoomType type) {
        return ChatRoomDto.builder()
                .chatRoomId(id)
                .name(name)
                .type(type)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}