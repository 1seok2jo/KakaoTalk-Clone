package oneseoktwojo.ohtalkhae.domain.chat.repository;

import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoom;
import oneseoktwojo.ohtalkhae.domain.chat.entity.ChatRoomMember;
import oneseoktwojo.ohtalkhae.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    
    List<ChatRoomMember> findByChatRoomChatRoomId(Long chatRoomId);
    
    Optional<ChatRoomMember> findByChatRoomChatRoomIdAndUserUserId(Long chatRoomId, Long userId);
    
    @Query("SELECT COUNT(m) FROM ChatRoomMember m WHERE m.chatRoom.chatRoomId = :chatRoomId")
    int countMembersByChatRoomId(@Param("chatRoomId") Long chatRoomId);
    
    @Query("SELECT m FROM ChatRoomMember m WHERE m.user.userId = :userId")
    List<ChatRoomMember> findAllByUserId(@Param("userId") Long userId);
    
    boolean existsByChatRoomChatRoomIdAndUserUserId(Long chatRoomId, Long userId);
}
