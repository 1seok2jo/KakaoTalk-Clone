package oneseoktwojo.ohtalkhae.domain.friend.service;

import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendListDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendRequestDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendResponseDto;

import java.util.List;

public interface FriendService {
    // 새 친구 요청 생성
    void sendRequest(FriendRequestDto dto);
    // 요청 수락 처리
    void acceptRequest(FriendResponseDto dto);
    // 요청 거절 처리
    void rejectRequest(FriendResponseDto dto);
    // 친구 관계 제거(양방향 모두)
    void deleteFriend(Long userId, Long targetId);
    // 수신된 모든 요청 조회
    List<FriendListDto> getReceivedRequests(Long userId);
    // 서로 수락된 친구 목록 조회
    List<FriendListDto> getFriendsList(Long userId);
}
