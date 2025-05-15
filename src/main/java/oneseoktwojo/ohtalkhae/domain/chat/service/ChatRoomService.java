package oneseoktwojo.ohtalkhae.domain.chat.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomDto;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomResponse;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoomMember;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.chat.enums.Role;
import oneseoktwojo.ohtalkhae.domain.chat.mapper.ChatMapper;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomMemberRepository;
import oneseoktwojo.ohtalkhae.domain.chat.repository.ChatRoomRepository;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 채팅방 관련 비즈니스 로직을 처리하는 서비스
 * 채팅방 생성, 조회, 수정, 멤버 관리 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 사용
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 채팅방 생성
     * 
     * @param request 채팅방 생성 요청 정보
     * @param creatorId 채팅방 생성자 ID
     * @return 생성된 채팅방 정보
     */
    @Transactional // 데이터 변경이 있으므로 쓰기 트랜잭션 사용
    public ChatRoomResponse createChatRoom(CreateChatRoomRequest request, Long creatorId) {
        // 입력값 유효성 검사
        validateChatRoomCreation(request.getName(), request.getMemberIds(), request.getType(), creatorId);

        Set<Long> uniqueMemberIds = new HashSet<>(request.getMemberIds());
        uniqueMemberIds.add(creatorId);

        // 참여자 유효성 검사 및 사용자 정보 조회
        List<User> members = userRepository.findAllById(uniqueMemberIds);
        if (members.size() != uniqueMemberIds.size()) {
            Set<Long> foundIds = members.stream().map(User::getUserId).collect(Collectors.toSet());
            uniqueMemberIds.removeAll(foundIds);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "다음 사용자 ID를 찾을 수 없습니다: " + uniqueMemberIds);
        }

        // 채팅방 유형별 검사 및 기존 채팅방 확인
        if (request.getType() == ChatRoomType.DIRECT) {
            if (uniqueMemberIds.size() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1:1 채팅방은 반드시 2명의 참여자가 필요합니다.");
            }
            Optional<ChatRoom> existingDirectRoom = chatRoomRepository.findDirectChatRoomByMemberIds(new ArrayList<>(uniqueMemberIds));
            if (existingDirectRoom.isPresent()) {
                return ChatRoomResponse.fromEntity(existingDirectRoom.get());
            }
        } else if (request.getType() == ChatRoomType.GROUP) {
            if (uniqueMemberIds.size() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "그룹 채팅방은 최소 2명 이상의 참여자가 필요합니다.");
            }
            if (!StringUtils.hasText(request.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "그룹 채팅방은 이름이 반드시 필요합니다.");
            }
        }

        // 채팅방 이름 설정 (1:1 채팅방, 이름 미지정 시)
        String chatRoomName = request.getName();
        if (request.getType() == ChatRoomType.DIRECT && !StringUtils.hasText(chatRoomName)) {
            chatRoomName = members.stream()
                    .sorted((u1, u2) -> u1.getUserId().compareTo(u2.getUserId()))
                    .map(User::getUsername)
                    .collect(Collectors.joining(", "));
        }

        // ChatRoom 엔티티 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .type(request.getType())
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // ChatRoomMember 엔티티 생성 및 저장
        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();
        for (User member : members) {
            Role role = member.getUserId().equals(creatorId) ? Role.OWNER : Role.MEMBER;
            ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                    .chatRoom(savedChatRoom)
                    .user(member)
                    .role(role)
                    .build();
            chatRoomMembers.add(chatRoomMember);
        }
        chatRoomMemberRepository.saveAll(chatRoomMembers);

        return ChatRoomResponse.fromEntity(savedChatRoom);
    }

    /**
     * 사용자가 속한 모든 채팅방 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 채팅방 목록
     */
    public List<ChatRoomDto> getUserChatRooms(Long userId) {
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByUserId(userId);
        return chatRoomMembers.stream()
                .map(member -> ChatMapper.toChatRoomDto(member.getChatRoom(), userId))
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 알림 설정 업데이트
     * 
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @param notificationEnabled 알림 활성화 여부
     */
    @Transactional
    public void updateNotificationSettings(Long roomId, Long userId, boolean notificationEnabled) {
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomChatRoomIdAndUserUserId(roomId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방 멤버를 찾을 수 없습니다."));
        
        member.updateNotificationSettings(notificationEnabled);
        chatRoomMemberRepository.save(member);
    }

    /**
     * 채팅방에 새 멤버 추가
     * 
     * @param roomId 채팅방 ID
     * @param userIds 추가할 사용자 ID 목록
     */
    @Transactional
    public void addMembersToChatRoom(Long roomId, List<Long> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        
        if (chatRoom.getType() == ChatRoomType.DIRECT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1:1 채팅방에는 새 멤버를 추가할 수 없습니다.");
        }
        
        // 이미 채팅방에 있는 사용자 필터링
        List<Long> existingMemberIds = chatRoomMemberRepository.findByChatRoomChatRoomId(roomId).stream()
                .map(member -> member.getUser().getUserId())
                .collect(Collectors.toList());
        
        List<Long> newMemberIds = userIds.stream()
                .filter(id -> !existingMemberIds.contains(id))
                .collect(Collectors.toList());
        
        if (newMemberIds.isEmpty()) {
            return;
        }
        
        List<User> newMembers = userRepository.findAllById(newMemberIds);
        if (newMembers.size() != newMemberIds.size()) {
            Set<Long> foundIds = newMembers.stream().map(User::getUserId).collect(Collectors.toSet());
            newMemberIds.removeAll(foundIds);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "다음 사용자 ID를 찾을 수 없습니다: " + newMemberIds);
        }
        
        List<ChatRoomMember> newChatRoomMembers = newMembers.stream()
                .map(user -> ChatRoomMember.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .role(Role.MEMBER)
                        .build())
                .collect(Collectors.toList());
        
        chatRoomMemberRepository.saveAll(newChatRoomMembers);
    }

    /**
     * 채팅방 나가기
     * 
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void leaveChatRoom(Long roomId, Long userId) {
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomChatRoomIdAndUserUserId(roomId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방 멤버를 찾을 수 없습니다."));
        
        // 1:1 채팅방인 경우 채팅방을 삭제하지 않고 멤버만 제거
        if (member.getChatRoom().getType() == ChatRoomType.DIRECT) {
            chatRoomMemberRepository.delete(member);
            return;
        }
        
        // 그룹 채팅방에서 마지막 멤버가 나가는 경우 채팅방 삭제
        long memberCount = chatRoomMemberRepository.countMembersByChatRoomId(roomId);
        if (memberCount <= 1) {
            chatRoomRepository.delete(member.getChatRoom());
            return;
        }
        
        // 그룹 채팅방에서 방장이 나가는 경우, 다른 멤버에게 방장 권한 위임
        if (member.getRole() == Role.OWNER) {
            List<ChatRoomMember> otherMembers = chatRoomMemberRepository.findByChatRoomChatRoomId(roomId).stream()
                    .filter(m -> !m.getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());
            
            if (!otherMembers.isEmpty()) {
                ChatRoomMember newOwner = otherMembers.get(0);
                newOwner.updateRole(Role.OWNER);
                chatRoomMemberRepository.save(newOwner);
            }
        }
        
        // 멤버 제거
        chatRoomMemberRepository.delete(member);
    }

    /**
     * 채팅방 이름 업데이트
     * 
     * @param roomId 채팅방 ID
     * @param newName 새 채팅방 이름
     */
    @Transactional
    public void updateChatRoomName(Long roomId, String newName) {
        if (!StringUtils.hasText(newName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채팅방 이름은 비어있을 수 없습니다.");
        }
        
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        
        if (chatRoom.getType() == ChatRoomType.DIRECT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1:1 채팅방의 이름은 변경할 수 없습니다.");
        }
        
        chatRoom.updateName(newName);
        chatRoomRepository.save(chatRoom);
    }
    
    /**
     * 채팅방 생성 파라미터 유효성 검증
     */
    private void validateChatRoomCreation(String name, List<Long> memberIds, ChatRoomType type, Long creatorId) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채팅방 참여자는 최소 1명 이상이어야 합니다.");
        }
        
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채팅방 유형은 필수입니다.");
        }
        
        if (type == ChatRoomType.GROUP && !StringUtils.hasText(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "그룹 채팅방은 이름이 필수입니다.");
        }
        
        if (creatorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채팅방 생성자 ID는 필수입니다.");
        }
    }
} 