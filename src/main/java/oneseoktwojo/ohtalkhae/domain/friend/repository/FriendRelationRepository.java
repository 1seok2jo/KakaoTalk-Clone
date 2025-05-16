package oneseoktwojo.ohtalkhae.domain.friend.repository;

import oneseoktwojo.ohtalkhae.domain.friend.entity.FriendRelation;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.friend.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {
    // 특정 사용자 간 관계 조회
    Optional<FriendRelation> findByRequesterAndReceiver(User requester, User receiver);
    // 받은 요청 조회
    List<FriendRelation> findByReceiverAndStatus(User receiver, FriendStatus status);
    // 보낸 요청 조회
    List<FriendRelation> findByRequesterAndStatus(User requester, FriendStatus status);
}
