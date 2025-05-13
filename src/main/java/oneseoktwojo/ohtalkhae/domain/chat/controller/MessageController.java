package oneseoktwojo.ohtalkhae.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.MessageRequest;
import oneseoktwojo.ohtalkhae.domain.chat.service.MessageService;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 관련 HTTP API 컨트롤러
 * 메시지 전송, 조회, 읽음 처리, 수정, 삭제 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 특정 채팅방에 메시지 전송
     * 
     * @param roomId 채팅방 ID
     * @param request 메시지 요청 정보 (내용, 답장 대상 메시지 ID, 멘션된 사용자 ID 목록)
     * @param currentUser 현재 인증된 사용자 정보
     * @return 저장된 메시지 정보
     */
    @PostMapping("/chatrooms/{roomId}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long roomId,
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
        }
        
        try {
            MessageDto savedMessage = messageService.sendMessage(request, currentUser.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
        } catch (Exception e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 채팅방의 메시지 목록 조회
     * 특정 시간 이전의 메시지를 페이징하여 조회할 수 있습니다.
     * 
     * @param roomId 채팅방 ID
     * @param before 이 시간 이전의 메시지를 조회 (null인 경우 최근 메시지 조회)
     * @param limit 조회할 최대 메시지 수
     * @param currentUser 현재 인증된 사용자 정보
     * @return 메시지 목록
     */
    @GetMapping("/chatrooms/{roomId}/messages")
    public ResponseEntity<List<MessageDto>> getChatRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) LocalDateTime before,
            @RequestParam(defaultValue = "30") Integer limit,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            if (before != null) {
                return ResponseEntity.ok(messageService.getMessagesBeforeTimestamp(roomId, before, limit));
            } else {
                return ResponseEntity.ok(messageService.getChatRoomMessages(roomId, limit));
            }
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    /**
     * 특정 메시지를 읽음으로 표시
     * 
     * @param roomId 채팅방 ID
     * @param messageId 읽음 표시할 메시지 ID
     * @param currentUser 현재 인증된 사용자 정보
     * @return 성공 시 빈 응답 (200 OK)
     */
    @PostMapping("/chatrooms/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long roomId,
            @RequestParam Long messageId,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            messageService.markMessageAsRead(messageId, currentUser.getUserId());
            return ResponseEntity.ok().build();
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    /**
     * 메시지 삭제
     * 
     * @param roomId 채팅방 ID
     * @param messageId 삭제할 메시지 ID
     * @param currentUser 현재 인증된 사용자 정보
     * @return 성공 시 빈 응답 (204 No Content)
     */
    @DeleteMapping("/chatrooms/{roomId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long roomId,
            @PathVariable Long messageId,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            messageService.deleteMessage(messageId, currentUser.getUserId());
            return ResponseEntity.noContent().build();
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    /**
     * 메시지 내용 수정
     * 
     * @param roomId 채팅방 ID
     * @param messageId 수정할 메시지 ID
     * @param newContent 새 메시지 내용
     * @param currentUser 현재 인증된 사용자 정보
     * @return 수정된 메시지 정보
     */
    @PatchMapping("/chatrooms/{roomId}/messages/{messageId}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long roomId,
            @PathVariable Long messageId,
            @RequestParam String newContent,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            MessageDto updatedMessage = messageService.updateMessage(messageId, newContent, currentUser.getUserId());
            return ResponseEntity.ok(updatedMessage);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    /**
     * 채팅방 내 메시지 검색
     * 
     * @param roomId 채팅방 ID
     * @param query 검색어
     * @param limit 검색 결과 최대 수
     * @param currentUser 현재 인증된 사용자 정보
     * @return 검색된 메시지 목록
     */
    @GetMapping("/chatrooms/{roomId}/search")
    public ResponseEntity<List<MessageDto>> searchMessages(
            @PathVariable Long roomId,
            @RequestParam String query,
            @RequestParam(defaultValue = "30") Integer limit,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            return ResponseEntity.ok(messageService.searchMessages(roomId, query, limit));
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    /**
     * 메시지 전달 (다른 채팅방으로 메시지 포워딩)
     * 
     * @param messageId 전달할 메시지 ID
     * @param targetRoomIds 메시지를 전달할 대상 채팅방 ID 목록
     * @param currentUser 현재 인증된 사용자 정보
     * @return 전달된 메시지 ID 목록
     */
    @PostMapping("/messages/forward")
    public ResponseEntity<List<Long>> forwardMessage(
            @RequestParam Long messageId,
            @RequestBody List<Long> targetRoomIds,
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다.");
            }
            List<Long> forwardedMessageIds = messageService.forwardMessage(messageId, targetRoomIds, currentUser.getUserId());
            return ResponseEntity.ok(forwardedMessageIds);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
        }
    }
} 