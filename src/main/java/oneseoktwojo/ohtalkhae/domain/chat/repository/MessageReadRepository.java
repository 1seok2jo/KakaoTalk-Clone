package oneseoktwojo.ohtalkhae.domain.chat.repository;

import oneseoktwojo.ohtalkhae.domain.chat.entity.MessageRead;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {
    
    Optional<MessageRead> findByMessageMessageIdAndUserUserId(Long messageId, Long userId);
    
    @Query("SELECT mr FROM MessageRead mr WHERE mr.message.chatRoom.chatRoomId = :chatRoomId AND mr.user.userId = :userId ORDER BY mr.readAt DESC")
    List<MessageRead> findByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(mr) FROM MessageRead mr WHERE mr.message.messageId = :messageId")
    int countByMessageId(@Param("messageId") Long messageId);
    
    boolean existsByMessageMessageIdAndUserUserId(Long messageId, Long userId);
    
    @Query("SELECT COUNT(mr) FROM MessageRead mr WHERE mr.message.messageId = :messageId")
    int countByMessageMessageId(@Param("messageId") Long messageId);
} 