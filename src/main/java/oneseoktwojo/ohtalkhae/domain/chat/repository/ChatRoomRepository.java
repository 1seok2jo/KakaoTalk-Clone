package oneseoktwojo.ohtalkhae.domain.chat.repository;

import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.userId = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);
    
    @Query("SELECT cr FROM ChatRoom cr " +
           "WHERE cr.type = oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType.DIRECT " +
           "AND SIZE(cr.members) = 2 " +
           "AND EXISTS (SELECT 1 FROM ChatRoomMember m1 WHERE m1.chatRoom = cr AND m1.user.userId = :user1Id) " +
           "AND EXISTS (SELECT 1 FROM ChatRoomMember m2 WHERE m2.chatRoom = cr AND m2.user.userId = :user2Id)")
    Optional<ChatRoom> findDirectChatRoomBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members crm1 JOIN cr.members crm2 " +
           "WHERE cr.type = :type " +
           "AND crm1.user.userId = :memberId1 " +
           "AND crm2.user.userId = :memberId2 " +
           "AND (SELECT COUNT(crm_inner) FROM ChatRoomMember crm_inner WHERE crm_inner.chatRoom = cr) = 2")
    Optional<ChatRoom> findDirectChatRoomByMembers(
            @Param("memberId1") Long memberId1,
            @Param("memberId2") Long memberId2,
            @Param("type") ChatRoomType type);

    // 편의 메소드 추가: List<Long>을 받아서 처리 (ID 순서 정렬 후 위 메소드 호출)
    default Optional<ChatRoom> findDirectChatRoomByMemberIds(List<Long> memberIds) {
        if (memberIds == null || memberIds.size() != 2) {
            return Optional.empty(); // ID가 2개가 아니면 DIRECT 룸이 아님
        }
        // ID를 정렬하여 항상 같은 순서로 쿼리하도록 보장
        memberIds.sort(Long::compareTo);
        return findDirectChatRoomByMembers(memberIds.get(0), memberIds.get(1), ChatRoomType.DIRECT);
    }
}
