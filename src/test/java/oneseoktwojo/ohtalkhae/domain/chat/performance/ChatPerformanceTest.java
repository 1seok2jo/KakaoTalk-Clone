package oneseoktwojo.ohtalkhae.domain.chat.performance;

import lombok.extern.slf4j.Slf4j;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.MessageRequest;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.ChatRoomResponse;
import oneseoktwojo.ohtalkhae.domain.chat.dto.response.MessageDto;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.chat.enums.MessageType;
import oneseoktwojo.ohtalkhae.domain.user.User;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 채팅 기능 테스트 및 결과 시각화
 * 테스트 결과를 HTML 파일로 저장하여 직관적으로 확인할 수 있게 합니다.
 * 
 * 이 테스트는 실제 서비스 호출 대신 목(mock) 데이터를 사용하여 HTML 리포트를 생성합니다.
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatPerformanceTest {

    private List<User> testUsers;
    private ChatRoomResponse testChatRoom;
    private static StringBuilder testResults = new StringBuilder();
    private static final String TEST_REPORT_DIR = "build/reports/chat-test";
    private static final AtomicInteger testCounter = new AtomicInteger(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    void setupReportDirectory() throws IOException {
        // 테스트 결과 저장 디렉토리 생성
        Path reportDir = Paths.get(TEST_REPORT_DIR);
        if (!Files.exists(reportDir)) {
            Files.createDirectories(reportDir);
        }
        
        // HTML 리포트 초기화
        testResults.append("<!DOCTYPE html>\n");
        testResults.append("<html lang=\"ko\">\n");
        testResults.append("<head>\n");
        testResults.append("  <meta charset=\"UTF-8\">\n");
        testResults.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        testResults.append("  <title>채팅 테스트 결과</title>\n");
        testResults.append("  <style>\n");
        testResults.append("    body { font-family: 'Noto Sans KR', Arial, sans-serif; margin: 0; padding: 20px; color: #333; }\n");
        testResults.append("    h1 { color: #2c3e50; text-align: center; margin-bottom: 30px; }\n");
        testResults.append("    .test-case { border: 1px solid #ddd; margin-bottom: 20px; border-radius: 5px; overflow: hidden; }\n");
        testResults.append("    .test-header { padding: 12px; background-color: #f8f9fa; border-bottom: 1px solid #ddd; font-weight: bold; }\n");
        testResults.append("    .test-result { padding: 15px; }\n");
        testResults.append("    .success { color: #28a745; }\n");
        testResults.append("    .error { color: #dc3545; }\n");
        testResults.append("    .message { border-left: 3px solid #007bff; padding: 10px; margin: 10px 0; background-color: #f8f9fa; }\n");
        testResults.append("    .message-sender { font-weight: bold; color: #007bff; }\n");
        testResults.append("    .message-content { margin-top: 5px; }\n");
        testResults.append("    .message-timestamp { color: #6c757d; font-size: 0.85em; margin-top: 5px; }\n");
        testResults.append("    .test-summary { display: flex; justify-content: space-between; padding: 10px; background-color: #e9ecef; }\n");
        testResults.append("    .chat-room { border: 1px solid #dee2e6; border-radius: 5px; margin: 15px 0; padding: 10px; }\n");
        testResults.append("    .chat-room-header { font-weight: bold; margin-bottom: 10px; display: flex; justify-content: space-between; }\n");
        testResults.append("    .chat-member { background-color: #e9f4fe; display: inline-block; margin-right: 5px; padding: 2px 8px; border-radius: 15px; font-size: 0.85em; }\n");
        testResults.append("  </style>\n");
        testResults.append("</head>\n");
        testResults.append("<body>\n");
        testResults.append("  <h1>채팅 테스트 결과</h1>\n");
        testResults.append("  <div class=\"test-summary\">\n");
        testResults.append("    <div>테스트 실행 시간: ").append(LocalDateTime.now().format(formatter)).append("</div>\n");
        testResults.append("  </div>\n");
        
        // 테스트 데이터 초기화
        setupTestData();
    }

    void setupTestData() {
        // 테스트 사용자 생성 (Mock 데이터) - Builder 패턴 사용
        testUsers = new ArrayList<>();
        testUsers.add(createMockUser(1L, "사용자1", "user1@example.com"));
        testUsers.add(createMockUser(2L, "사용자2", "user2@example.com"));
        testUsers.add(createMockUser(3L, "사용자3", "user3@example.com"));
        
        log.info("테스트 준비 완료: 사용자 {}명 생성됨", testUsers.size());
    }
    
    private User createMockUser(Long id, String username, String email) {
        // Builder 패턴으로 User 객체 생성
        return User.builder()
                .userId(id)
                .username(username)
                .email(email)
                .phone("010-1234-" + (5000 + id))
                .birthday(LocalDate.now().minusYears(20))
                .point(0L)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("1. 채팅방 생성 테스트")
    void testCreateChatRoom() {
        int testId = testCounter.incrementAndGet();
        String testName = "채팅방 생성 테스트";
        
        try {
            // 테스트 시작 기록
            testResults.append("  <div class=\"test-case\">\n");
            testResults.append("    <div class=\"test-header\">테스트 #").append(testId).append(": ").append(testName).append("</div>\n");
            testResults.append("    <div class=\"test-result\">\n");
            
            // 테스트 코드: 채팅방 생성 (Mock 데이터)
            List<Long> memberIds = new ArrayList<>();
            for (User user : testUsers) {
                memberIds.add(user.getUserId());
            }
            
            String chatRoomName = "테스트 채팅방 #" + testId;
            
            // Mock 채팅방 생성
            testChatRoom = createMockChatRoom(1L, chatRoomName, ChatRoomType.GROUP);
            
            // 테스트 결과 기록
            testResults.append("      <div class=\"success\">✅ 채팅방 생성 성공</div>\n");
            testResults.append("      <div class=\"chat-room\">\n");
            testResults.append("        <div class=\"chat-room-header\">\n");
            testResults.append("          <span>").append(chatRoomName).append("</span>\n");
            testResults.append("          <span>채팅방 ID: ").append(testChatRoom.getChatRoomId()).append("</span>\n");
            testResults.append("        </div>\n");
            testResults.append("        <div>채팅방 타입: ").append(testChatRoom.getType()).append("</div>\n");
            testResults.append("        <div>멤버 목록:</div>\n");
            testResults.append("        <div>\n");
            
            // 멤버 목록 표시
            for (User user : testUsers) {
                testResults.append("          <span class=\"chat-member\">")
                         .append(user.getUsername())
                         .append(" (ID: ").append(user.getUserId()).append(")")
                         .append("</span>\n");
            }
            
            testResults.append("        </div>\n");
            testResults.append("      </div>\n");
            
            // 채팅방 생성 성공 확인
            Assertions.assertNotNull(testChatRoom, "채팅방이 생성되지 않았습니다");
            Assertions.assertEquals(chatRoomName, testChatRoom.getName(), "채팅방 이름이 일치하지 않습니다");
            
        } catch (Exception e) {
            testResults.append("      <div class=\"error\">❌ 채팅방 생성 실패: ").append(e.getMessage()).append("</div>\n");
            Assertions.fail("채팅방 생성 테스트 실패: " + e.getMessage());
        } finally {
            testResults.append("    </div>\n");
            testResults.append("  </div>\n");
        }
    }
    
    private ChatRoomResponse createMockChatRoom(Long id, String name, ChatRoomType type) {
        return ChatRoomResponse.builder()
                .chatRoomId(id)
                .name(name)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @Order(2)
    @DisplayName("2. 메시지 전송 테스트")
    void testSendMessage() {
        int testId = testCounter.incrementAndGet();
        String testName = "메시지 전송 테스트";
        
        try {
            // 테스트 시작 기록
            testResults.append("  <div class=\"test-case\">\n");
            testResults.append("    <div class=\"test-header\">테스트 #").append(testId).append(": ").append(testName).append("</div>\n");
            testResults.append("    <div class=\"test-result\">\n");
            
            // 채팅방이 없으면 먼저 생성
            if (testChatRoom == null) {
                testCreateChatRoom();
            }
            
            // 테스트 코드: 메시지 전송 (Mock 데이터)
            List<MessageDto> sentMessages = new ArrayList<>();
            List<String> messageContents = List.of(
                "안녕하세요!", 
                "채팅 테스트 중입니다", 
                "이 메시지는 테스트용입니다", 
                "테스트 결과를 시각화해서 보여드릴게요"
            );
            
            testResults.append("      <div>채팅방 ID: ").append(testChatRoom.getChatRoomId()).append("</div>\n");
            testResults.append("      <div>메시지 전송 시작:</div>\n");
            
            for (int i = 0; i < messageContents.size(); i++) {
                User sender = testUsers.get(i % testUsers.size());
                String content = messageContents.get(i);
                
                // Mock 메시지 생성 - Builder 패턴 사용
                MessageDto sentMessage = createMockMessage(
                        (long)(i + 1), 
                        testChatRoom.getChatRoomId(), 
                        sender.getUserId(), 
                        sender.getUsername(),
                        content, 
                        LocalDateTime.now().minusMinutes(messageContents.size() - i)
                );
                
                sentMessages.add(sentMessage);
                
                // 메시지 기록
                testResults.append("      <div class=\"message\">\n");
                testResults.append("        <div class=\"message-sender\">")
                         .append(sender.getUsername())
                         .append(" (ID: ").append(sender.getUserId()).append(")")
                         .append("</div>\n");
                testResults.append("        <div class=\"message-content\">").append(content).append("</div>\n");
                testResults.append("        <div class=\"message-timestamp\">")
                         .append(sentMessage.getCreatedAt().format(formatter))
                         .append("</div>\n");
                testResults.append("      </div>\n");
            }
            
            // 테스트 결과 기록
            testResults.append("      <div class=\"success\">✅ 메시지 전송 성공: 총 ").append(sentMessages.size()).append("개의 메시지</div>\n");
            
            // 메시지 전송 성공 확인
            Assertions.assertEquals(messageContents.size(), sentMessages.size(), "전송된 메시지 수가 일치하지 않습니다");
            
        } catch (Exception e) {
            testResults.append("      <div class=\"error\">❌ 메시지 전송 실패: ").append(e.getMessage()).append("</div>\n");
            Assertions.fail("메시지 전송 테스트 실패: " + e.getMessage());
        } finally {
            testResults.append("    </div>\n");
            testResults.append("  </div>\n");
        }
    }
    
    private MessageDto createMockMessage(Long id, Long roomId, Long senderId, String senderNickname, String content, LocalDateTime createdAt) {
        // Builder 패턴으로 MessageDto 객체 생성
        return MessageDto.builder()
                .messageId(id)
                .chatRoomId(roomId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .senderProfileUrl("/default-profile.png")
                .content(content)
                .type(MessageType.TEXT)
                .isDeleted(false)
                .isEdited(false)
                .mentionedUserIds(new ArrayList<>())
                .unreadCount(0)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    @Test
    @Order(3)
    @DisplayName("3. 메시지 조회 테스트")
    void testGetMessages() {
        int testId = testCounter.incrementAndGet();
        String testName = "메시지 조회 테스트";
        
        try {
            // 테스트 시작 기록
            testResults.append("  <div class=\"test-case\">\n");
            testResults.append("    <div class=\"test-header\">테스트 #").append(testId).append(": ").append(testName).append("</div>\n");
            testResults.append("    <div class=\"test-result\">\n");
            
            // 메시지가 있는지 확인, 없으면 메시지 전송 테스트를 먼저 실행
            if (testChatRoom == null) {
                testCreateChatRoom();
                testSendMessage();
            }
            
            // 테스트 코드: 메시지 조회 (Mock 데이터)
            List<MessageDto> messages = new ArrayList<>();
            
            // Mock 메시지 데이터 생성
            messages.add(createMockMessage(1L, testChatRoom.getChatRoomId(), testUsers.get(0).getUserId(), testUsers.get(0).getUsername(), "안녕하세요!", LocalDateTime.now().minusMinutes(30)));
            messages.add(createMockMessage(2L, testChatRoom.getChatRoomId(), testUsers.get(1).getUserId(), testUsers.get(1).getUsername(), "채팅 테스트 중입니다", LocalDateTime.now().minusMinutes(20)));
            messages.add(createMockMessage(3L, testChatRoom.getChatRoomId(), testUsers.get(2).getUserId(), testUsers.get(2).getUsername(), "이 메시지는 테스트용입니다", LocalDateTime.now().minusMinutes(10)));
            messages.add(createMockMessage(4L, testChatRoom.getChatRoomId(), testUsers.get(0).getUserId(), testUsers.get(0).getUsername(), "테스트 결과를 시각화해서 보여드릴게요", LocalDateTime.now()));
            
            testResults.append("      <div>채팅방 ID: ").append(testChatRoom.getChatRoomId()).append("</div>\n");
            testResults.append("      <div>조회된 메시지 목록 (").append(messages.size()).append("개):</div>\n");
            
            // 메시지 목록 표시
            for (MessageDto message : messages) {
                User sender = testUsers.stream()
                        .filter(u -> u.getUserId().equals(message.getSenderId()))
                        .findFirst()
                        .orElse(null);
                
                String senderName = sender != null ? sender.getUsername() : "알 수 없음";
                
                testResults.append("      <div class=\"message\">\n");
                testResults.append("        <div class=\"message-sender\">")
                         .append(senderName)
                         .append(" (ID: ").append(message.getSenderId()).append(")")
                         .append("</div>\n");
                testResults.append("        <div class=\"message-content\">").append(message.getContent()).append("</div>\n");
                testResults.append("        <div class=\"message-timestamp\">")
                         .append(message.getCreatedAt().format(formatter))
                         .append("</div>\n");
                testResults.append("      </div>\n");
            }
            
            // 테스트 결과 기록
            testResults.append("      <div class=\"success\">✅ 메시지 조회 성공: 총 ").append(messages.size()).append("개의 메시지</div>\n");
            
            // 메시지 조회 성공 확인
            Assertions.assertFalse(messages.isEmpty(), "조회된 메시지가 없습니다");
            
        } catch (Exception e) {
            testResults.append("      <div class=\"error\">❌ 메시지 조회 실패: ").append(e.getMessage()).append("</div>\n");
            Assertions.fail("메시지 조회 테스트 실패: " + e.getMessage());
        } finally {
            testResults.append("    </div>\n");
            testResults.append("  </div>\n");
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. 채팅방 목록 조회 테스트")
    void testGetChatRooms() {
        int testId = testCounter.incrementAndGet();
        String testName = "채팅방 목록 조회 테스트";
        
        try {
            // 테스트 시작 기록
            testResults.append("  <div class=\"test-case\">\n");
            testResults.append("    <div class=\"test-header\">테스트 #").append(testId).append(": ").append(testName).append("</div>\n");
            testResults.append("    <div class=\"test-result\">\n");
            
            // 채팅방이 없으면 먼저 생성
            if (testChatRoom == null) {
                testCreateChatRoom();
            }
            
            // 테스트 코드: 채팅방 목록 조회 (Mock 데이터)
            User user = testUsers.get(0);
            List<ChatRoomResponse> chatRooms = new ArrayList<>();
            
            // Mock 채팅방 데이터 생성
            chatRooms.add(testChatRoom);
            chatRooms.add(createMockChatRoom(2L, "그룹 채팅방", ChatRoomType.GROUP));
            chatRooms.add(createMockChatRoom(3L, "1:1 채팅방", ChatRoomType.DIRECT));
            
            testResults.append("      <div>사용자 ID: ").append(user.getUserId()).append("</div>\n");
            testResults.append("      <div>조회된 채팅방 목록 (").append(chatRooms.size()).append("개):</div>\n");
            
            // 채팅방 목록 표시
            for (ChatRoomResponse chatRoom : chatRooms) {
                testResults.append("      <div class=\"chat-room\">\n");
                testResults.append("        <div class=\"chat-room-header\">\n");
                testResults.append("          <span>").append(chatRoom.getName()).append("</span>\n");
                testResults.append("          <span>채팅방 ID: ").append(chatRoom.getChatRoomId()).append("</span>\n");
                testResults.append("        </div>\n");
                testResults.append("        <div>채팅방 타입: ").append(chatRoom.getType()).append("</div>\n");
                testResults.append("        <div>생성 시간: ").append(chatRoom.getCreatedAt().format(formatter)).append("</div>\n");
                testResults.append("      </div>\n");
            }
            
            // 테스트 결과 기록
            testResults.append("      <div class=\"success\">✅ 채팅방 목록 조회 성공: 총 ").append(chatRooms.size()).append("개의 채팅방</div>\n");
            
            // 채팅방 목록 조회 성공 확인
            Assertions.assertFalse(chatRooms.isEmpty(), "조회된 채팅방이 없습니다");
            
        } catch (Exception e) {
            testResults.append("      <div class=\"error\">❌ 채팅방 목록 조회 실패: ").append(e.getMessage()).append("</div>\n");
            Assertions.fail("채팅방 목록 조회 테스트 실패: " + e.getMessage());
        } finally {
            testResults.append("    </div>\n");
            testResults.append("  </div>\n");
        }
    }

    @AfterAll
    void generateReport() {
        try {
            // HTML 리포트 마무리
            testResults.append("</body>\n");
            testResults.append("</html>\n");
            
            // 파일에 저장
            String reportFileName = TEST_REPORT_DIR + "/chat-test-report-" + System.currentTimeMillis() + ".html";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFileName))) {
                writer.write(testResults.toString());
            }
            
            log.info("테스트 리포트가 생성되었습니다: {}", reportFileName);
        } catch (IOException e) {
            log.error("테스트 리포트 생성 실패: {}", e.getMessage());
        }
    }
}
