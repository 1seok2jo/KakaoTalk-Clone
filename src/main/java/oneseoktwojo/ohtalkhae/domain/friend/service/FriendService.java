package oneseoktwojo.ohtalkhae.domain.friend.service;

import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendListDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendRequestDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendResponseDto;

import java.util.List;

public interface FriendService {
    void sendRequest(FriendRequestDto dto);
    void acceptRequest(FriendResponseDto dto);
    void rejectRequest(FriendResponseDto dto);
    void deleteFriend(Long userId, Long targetId);
    List<FriendListDto> getReceivedRequests(Long userId);
    List<FriendListDto> getFriendsList(Long userId);
}
