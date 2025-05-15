package oneseoktwojo.ohtalkhae.domain.friend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendListDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendRequestDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendResponseDto;
import oneseoktwojo.ohtalkhae.domain.friend.entity.FriendRelation;
import oneseoktwojo.ohtalkhae.domain.friend.enums.FriendStatus;
import oneseoktwojo.ohtalkhae.domain.friend.repository.FriendRelationRepository;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.DuplicateFriendRequestException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.FriendRequestNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.SelfFriendRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


class FriendServiceImplTest {
    @Mock private FriendRelationRepository relationRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks
    private FriendServiceImpl service;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alice = User.builder().userId(1L).username("alice").build();
        bob   = User.builder().userId(2L).username("bob").build();
    }

    // 정상 흐름 저장 검증
    @Test
    void sendRequest_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(relationRepository.findByRequesterAndReceiver(alice, bob)).thenReturn(Optional.empty());

        service.sendRequest(new FriendRequestDto(1L, 2L));

        verify(relationRepository).save(any(FriendRelation.class));
    }

    // 본인에게 요청 시도시
    @Test
    void sendRequest_selfThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        assertThrows(SelfFriendRequestException.class, () ->
                service.sendRequest(new FriendRequestDto(1L, 1L))
        );
    }

    // PENDING 요청이 없을 때 빈 리스트 반환 검증
    @Test
    void getReceivedRequests_emptyList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(relationRepository.findByReceiverAndStatus(bob, FriendStatus.PENDING))
                .thenReturn(Collections.emptyList());

        List<FriendListDto> list = service.getReceivedRequests(2L);
        assertTrue(list.isEmpty());
    }


    // 이미 PENDING 요청이 있을 때
    @Test
    void sendRequest_duplicateThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        FriendRelation existing = FriendRelation.builder()
                .requester(alice)
                .receiver(bob)
                .status(FriendStatus.PENDING)
                .build();
        when(relationRepository.findByRequesterAndReceiver(alice, bob))
                .thenReturn(Optional.of(existing));

        assertThrows(DuplicateFriendRequestException.class, () ->
                service.sendRequest(new FriendRequestDto(1L, 2L))
        );
    }

    // 존재하지 않는 요청 수락 시
    @Test
    void acceptRequest_notFoundThrows() {
        when(relationRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(FriendRequestNotFoundException.class, () ->
                service.acceptRequest(new FriendResponseDto(10L))
        );
    }

    // 존재하지 않는 요청 거절 시
    @Test
    void rejectRequest_notFoundThrows() {
        when(relationRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(FriendRequestNotFoundException.class, () ->
                service.rejectRequest(new FriendResponseDto(10L))
        );
    }

    // 삭제할 관계가 없을 때
    @Test
    void deleteFriend_notFoundThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(relationRepository.findByRequesterAndReceiver(alice, bob)).thenReturn(Optional.empty());
        when(relationRepository.findByRequesterAndReceiver(bob, alice)).thenReturn(Optional.empty());

        assertThrows(FriendRequestNotFoundException.class, () ->
                service.deleteFriend(1L, 2L)
        );
    }


}