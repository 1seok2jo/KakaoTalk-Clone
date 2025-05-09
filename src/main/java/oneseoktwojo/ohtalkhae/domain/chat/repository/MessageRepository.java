package oneseoktwojo.ohtalkhae.domain.chat.repository;

import oneseoktwojo.ohtalkhae.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId ORDER BY m.createdAt DESC")
    List<Message> findByChatRoomIdOrderByCreatedAtDesc(@Param("chatRoomId") Long chatRoomId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId AND m.createdAt < :before ORDER BY m.createdAt DESC")
    List<Message> findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            @Param("chatRoomId") Long chatRoomId, 
            @Param("before") LocalDateTime before, 
            Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId AND m.createdAt > :since ORDER BY m.createdAt ASC")
    List<Message> findByChatRoomIdAndCreatedAtAfterOrderByCreatedAtAsc(
            @Param("chatRoomId") Long chatRoomId, 
            @Param("since") LocalDateTime since, 
            Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY m.createdAt DESC")
    List<Message> searchInChatRoom(
            @Param("chatRoomId") Long chatRoomId, 
            @Param("query") String query, 
            Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId AND m.createdAt > (SELECT COALESCE(MAX(crm.lastReadMessage.createdAt), '1970-01-01') FROM ChatRoomMember crm WHERE crm.chatRoom.chatRoomId = :chatRoomId AND crm.user.userId = :userId)")
    int countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(mr) FROM MessageRead mr WHERE mr.message.messageId = :messageId")
    int countReadByMessageId(@Param("messageId") Long messageId);
}
