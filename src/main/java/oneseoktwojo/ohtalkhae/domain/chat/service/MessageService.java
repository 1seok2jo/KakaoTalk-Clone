package oneseoktwojo.ohtalkhae.domain.chat.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.Message;
import oneseoktwojo.ohtalkhae.domain.chat.entity.MessageRead;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.MessageRequest;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import oneseoktwojo.ohtalkhae.domain.chat.mapper.ChatMapper;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomMemberRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.MessageReadRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.MessageRepository;
import oneseoktwojo.ohtalkhae.domain.user.User;
import oneseoktwojo.ohtalkhae.domain.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메시지 관련 비즈니스 로직을 처리하는 서비스
 * 메시지 전송, 조회, 읽음 처리, 수정, 삭제 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 사용
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    /**
     * 새 메시지 전송
     * 
     * @param request 메시지 요청 정보
     * @param senderId 발신자 ID
     * @return 생성된 메시지 정보
     */
    @Transactional // 데이터 변경이 있으므로 쓰기 트랜잭션 사용
    public MessageDto sendMessage(MessageRequest request, Long senderId) {
        // 채팅방, 사용자 유효성 검사
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
                
        // 사용자가 채팅방 멤버인지 확인
        boolean isMember = chatRoomMemberRepository
            .existsByChatRoomChatRoomIdAndUserUserId(request.getRoomId(), senderId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 속한 멤버만 메시지를 보낼 수 있습니다.");
        }
        
        // 답장 메시지 확인 (있는 경우)
        Message replyToMessage = null;
        if (request.getReplyToMessageId() != null) {
            replyToMessage = messageRepository.findById(request.getReplyToMessageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답장 대상 메시지를 찾을 수 없습니다."));
        }
        
        // 메시지 생성
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .type(MessageType.TEXT)
                .replyToMessage(replyToMessage)
                .mentionedUserIds(request.getMentionedUserIds() != null ? request.getMentionedUserIds() : new ArrayList<>())
                .build();
                
        // 메시지 저장
        message = messageRepository.save(message);
        
        // 메시지를 읽음으로 표시 (보낸 사람은 자동으로 읽음)
        markAsRead(message, sender);
        
        // 멘션된 사용자 처리
        if (request.getMentionedUserIds() != null && !request.getMentionedUserIds().isEmpty()) {
            // TODO: 멘션된 사용자에게 알림 보내기 (향후 구현)
        }
        
        // DTO 변환 및 반환
        int unreadCount = calculateUnreadCount(message);
        return ChatMapper.toMessageDto(message, unreadCount);
    }
    
    /**
     * 메시지 읽음 표시
     * 
     * @param messageId 메시지 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        
        // 이미 읽은 메시지인지 확인
        boolean alreadyRead = messageReadRepository.existsByMessageMessageIdAndUserUserId(messageId, userId);
        if (!alreadyRead) {
            markAsRead(message, user);
        }
    }
    
    /**
     * 채팅방의 이전 메시지 가져오기
     * 
     * @param roomId 채팅방 ID
     * @param before 이 시간 이전의 메시지를 조회
     * @param limit 조회할 최대 메시지 수
     * @return 메시지 목록
     */
    public List<MessageDto> getMessagesBeforeTimestamp(Long roomId, LocalDateTime before, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Message> messages = messageRepository.findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                roomId, before, pageRequest);
        
        return messages.stream()
                .map(message -> ChatMapper.toMessageDto(message, calculateUnreadCount(message)))
                .collect(Collectors.toList());
    }
    
    /**
     * 채팅방의 최근 메시지 가져오기
     * 
     * @param roomId 채팅방 ID
     * @param limit 조회할 최대 메시지 수
     * @return 메시지 목록
     */
    public List<MessageDto> getChatRoomMessages(Long roomId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageRequest);
        
        return messages.stream()
                .map(message -> ChatMapper.toMessageDto(message, calculateUnreadCount(message)))
                .collect(Collectors.toList());
    }
    
    /**
     * 메시지 검색
     * 
     * @param roomId 채팅방 ID
     * @param query 검색어
     * @param limit 검색 결과 최대 수
     * @return 검색된 메시지 목록
     */
    public List<MessageDto> searchMessages(Long roomId, String query, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Message> messages = messageRepository.searchInChatRoom(roomId, query, pageRequest);
        
        return messages.stream()
                .map(message -> ChatMapper.toMessageDto(message, calculateUnreadCount(message)))
                .collect(Collectors.toList());
    }
    
    /**
     * 메시지 삭제
     * 
     * @param messageId 메시지 ID
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."));
                
        if (!message.isEditableBy(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 보낸 메시지만 삭제할 수 있습니다.");
        }
        
        message.markAsDeleted();
        messageRepository.save(message);
    }
    
    /**
     * 메시지 내용 수정
     * 
     * @param messageId 메시지 ID
     * @param newContent 새 메시지 내용
     * @param userId 요청한 사용자 ID
     * @return 수정된 메시지 정보
     */
    @Transactional
    public MessageDto updateMessage(Long messageId, String newContent, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."));
                
        if (!message.isEditableBy(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 보낸 메시지만 수정할 수 있습니다.");
        }
        
        message.updateContent(newContent);
        message = messageRepository.save(message);
        
        return ChatMapper.toMessageDto(message, calculateUnreadCount(message));
    }
    
    /**
     * 메시지 전달 (다른 채팅방으로 메시지 포워딩)
     * 
     * @param messageId 전달할 메시지 ID
     * @param targetRoomIds 메시지를 전달할 대상 채팅방 ID 목록
     * @param userId 요청한 사용자 ID
     * @return 전달된 메시지 ID 목록
     */
    @Transactional
    public List<Long> forwardMessage(Long messageId, List<Long> targetRoomIds, Long userId) {
        Message originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."));
                
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
                
        List<Long> forwardedMessageIds = new ArrayList<>();
        
        for (Long roomId : targetRoomIds) {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다: " + roomId));
                    
            // 사용자가 해당 채팅방의 멤버인지 확인
            boolean isMember = chatRoomMemberRepository.existsByChatRoomChatRoomIdAndUserUserId(roomId, userId);
            if (!isMember) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 " + roomId + "의 멤버가 아닙니다.");
            }
            
            // 전달된 메시지 생성
            Message forwardedMessage = Message.builder()
                    .chatRoom(chatRoom)
                    .sender(user)
                    .content(originalMessage.getContent())
                    .type(originalMessage.getType())
                    .build();
                    
            forwardedMessage = messageRepository.save(forwardedMessage);
            markAsRead(forwardedMessage, user);
            
            forwardedMessageIds.add(forwardedMessage.getMessageId());
        }
        
        return forwardedMessageIds;
    }
    
    /**
     * 메시지 읽음 표시 헬퍼 메서드
     * 
     * @param message 읽을 메시지
     * @param user 읽는 사용자
     */
    private void markAsRead(Message message, User user) {
        MessageRead messageRead = MessageRead.builder()
                .message(message)
                .user(user)
                .readAt(LocalDateTime.now())
                .build();
        messageReadRepository.save(messageRead);
    }
    
    /**
     * 안 읽은 메시지 수 계산
     * 
     * @param message 대상 메시지
     * @return 안 읽은 사용자 수
     */
    private int calculateUnreadCount(Message message) {
        int totalMembers = chatRoomMemberRepository.countMembersByChatRoomId(message.getChatRoom().getChatRoomId());
        int readCount = messageReadRepository.countByMessageMessageId(message.getMessageId());
        return totalMembers - readCount;
    }
}

