package oneseoktwojo.ohtalkhae.domain.friend.service;

import lombok.RequiredArgsConstructor;
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
import oneseoktwojo.ohtalkhae.global.exception.friendexception.ResourceNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.SelfFriendRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendServiceImpl implements FriendService{
    private final FriendRelationRepository relationRepo;
    private final UserRepository userRepo;

    @Override
    public void sendRequest(FriendRequestDto dto) {
        // 1) 사용자 존재 여부 확인
        User from = userRepo.findById(dto.getFromId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User to = userRepo.findById(dto.getToId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // 2) 자기 자신 요청 방지
        if (from.equals(to)) throw new SelfFriendRequestException();
        // 3) 기존 요청 중복 검사 (REJECTED 제외)
        relationRepo.findByRequesterAndReceiver(from, to).ifPresent(fr -> {
            if (fr.getStatus() != FriendStatus.REJECTED) throw new DuplicateFriendRequestException("요청 중복");
        });
        // 4) PENDING 상태로 새 요청 저장
        FriendRelation fr = FriendRelation.builder()
                .requester(from)
                .receiver(to)
                .status(FriendStatus.PENDING)
                .build();
        relationRepo.save(fr);
    }

    @Override
    public void acceptRequest(FriendResponseDto dto) {
        // 요청 엔티티 조회 후 상태 ACCEPTED 로 변경
        FriendRelation fr = relationRepo.findById(dto.getRequestId())
                .orElseThrow(() -> new FriendRequestNotFoundException("요청을 찾을 수 없음"));
        fr.setStatus(FriendStatus.ACCEPTED);
    }

    @Override
    public void rejectRequest(FriendResponseDto dto) {
        // 요청 엔티티 조회 후 상태 REJECTED 로 변경
        FriendRelation fr = relationRepo.findById(dto.getRequestId())
                .orElseThrow(() -> new FriendRequestNotFoundException("요청을 찾을 수 없음"));
        fr.setStatus(FriendStatus.REJECTED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendListDto> getReceivedRequests(Long userId) {
        // 수신된 PENDING 요청만 조회하여 DTO로 변환
        User me = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return relationRepo.findByReceiverAndStatus(me, FriendStatus.PENDING)
                .stream()
                .map(fr -> new FriendListDto(
                        fr.getId(),
                        fr.getRequester().getNickname(),
                        fr.getRequester().getProfileImagePath(),
                        fr.getRequester().getUsername()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendListDto> getFriendsList(Long userId) {
        // 내가 수락한(ACCEPTED) 받은/보낸 요청을 합쳐 친구 목록 생성
        User me = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<FriendRelation> received = relationRepo.findByReceiverAndStatus(me, FriendStatus.ACCEPTED);
        List<FriendRelation> sent = relationRepo.findByRequesterAndStatus(me, FriendStatus.ACCEPTED);
        return Stream.concat(received.stream(), sent.stream())
                .map(fr -> {
                    User other = fr.getRequester().equals(me) ? fr.getReceiver() : fr.getRequester();
                    return new FriendListDto(
                            other.getUserId(),
                            other.getNickname(),
                            other.getProfileImagePath(),
                            other.getUsername()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(Long userId, Long targetId) {
        // 양방향 관계 탐색 후 삭제, 없으면 예외
        User me = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User other = userRepo.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FriendRelation fr = relationRepo
                .findByRequesterAndReceiver(me, other)
                .orElseGet(() -> relationRepo.findByRequesterAndReceiver(other, me)
                        .orElseThrow(() -> new FriendRequestNotFoundException("요청을 찾을 수 없음")));
        relationRepo.delete(fr);
    }

}
