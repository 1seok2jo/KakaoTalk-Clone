package oneseoktwojo.ohtalkhae.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomResponse;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.service.ChatRoomService;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 채팅방 관련 HTTP API 컨트롤러
 * 채팅방의 생성, 조회, 수정, 멤버 관리 등의 기능을 제공합니다.
 * 주요 기능: 채팅방 생성, 사용자별 채팅방 목록 조회, 채팅방 이름 수정, 멤버 추가/삭제
 */
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    /**
     * 새로운 채팅방 생성
     * 
     * @param request 채팅방 생성 요청 정보 (이름, 멤버 ID 목록, 채팅방 타입)
     * @param currentUser 현재 인증된 사용자 정보
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        ChatRoomResponse response = chatRoomService.createChatRoom(request, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 현재 사용자가 속한 모든 채팅방 목록 조회
     * 
     * @param currentUser 현재 인증된 사용자 정보
     * @return 사용자가 속한 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getUserChatRooms(
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        List<ChatRoomDto> chatRooms = chatRoomService.getUserChatRooms(currentUser.getUserId());
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * 채팅방 이름 업데이트
     * 
     * @param roomId 채팅방 ID
     * @param newName 새 채팅방 이름
     * @return 성공 시 빈 응답 (200 OK)
     */
    @PatchMapping("/{roomId}")
    public ResponseEntity<Void> updateChatRoomName(
            @PathVariable Long roomId,
            @RequestParam String newName,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        chatRoomService.updateChatRoomName(roomId, newName);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방에 새 멤버 추가
     * 
     * @param roomId 채팅방 ID
     * @param userIds 추가할 사용자 ID 목록
     * @return 성공 시 빈 응답 (200 OK)
     */
    @PostMapping("/{roomId}/members")
    public ResponseEntity<Void> addMembersToChatRoom(
            @PathVariable Long roomId,
            @RequestBody List<Long> userIds,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        chatRoomService.addMembersToChatRoom(roomId, userIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 사용자가 채팅방 나가기
     * 
     * @param roomId 채팅방 ID
     * @param currentUser 현재 인증된 사용자 정보
     * @return 성공 시 빈 응답 (204 No Content)
     */
    @DeleteMapping("/{roomId}/members/me")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable Long roomId, 
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        chatRoomService.leaveChatRoom(roomId, currentUser.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 채팅방 알림 설정 업데이트
     * 
     * @param roomId 채팅방 ID
     * @param notificationEnabled 알림 활성화 여부
     * @param currentUser 현재 인증된 사용자 정보
     * @return 성공 시 빈 응답 (200 OK)
     */
    @PutMapping("/{roomId}/members/me/notification")
    public ResponseEntity<Void> updateNotificationSettings(
            @PathVariable Long roomId,
            @RequestParam boolean notificationEnabled,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        chatRoomService.updateNotificationSettings(roomId, currentUser.getUserId(), notificationEnabled);
        return ResponseEntity.ok().build();
    }
}

