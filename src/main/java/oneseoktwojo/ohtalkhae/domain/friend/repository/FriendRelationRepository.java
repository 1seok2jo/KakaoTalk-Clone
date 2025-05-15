package oneseoktwojo.ohtalkhae.domain.friend.repository;

import oneseoktwojo.ohtalkhae.domain.friend.entity.FriendRelation;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// User 클래스 변경 or 확인 필요 !!!!

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {
    Optional<FriendRelation> findByRequesterAndReceiver(User requester, User receiver);
    List<FriendRelation> findByReceiverAndStatus(User receiver, User status);
    List<FriendRelation> findByRequesterAndStatus(User requester, User status);
}
