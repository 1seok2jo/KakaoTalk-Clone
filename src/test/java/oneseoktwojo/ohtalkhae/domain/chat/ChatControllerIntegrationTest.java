package oneseoktwojo.ohtalkhae.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import oneseoktwojo.ohtalkhae.domain.chat.dto.request.CreateChatRoomRequest;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import oneseoktwojo.ohtalkhae.domain.user.User;
import oneseoktwojo.ohtalkhae.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트 후 롤백
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 사용자 데이터 미리 저장
        testUser1 = User.builder().username("user1").email("user1@test.com").password("password").phone("010-1111-1111").role(Role.ROLE_USER).build();
        testUser2 = User.builder().username("user2").email("user2@test.com").password("password").phone("010-2222-2222").role(Role.ROLE_USER).build();
        testUser3 = User.builder().username("user3").email("user3@test.com").password("password").phone("010-3333-3333").role(Role.ROLE_USER).build();
        
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
    }

    @Test
    @DisplayName("그룹 채팅방 생성 성공")
    @WithMockUser(username = "user1@test.com") // testUser1로 인증된 상태 시뮬레이션
    void testCreateGroupChatRoom_Success() throws Exception {
        // Given
        List<Long> memberIds = Arrays.asList(testUser2.getUserId(), testUser3.getUserId());
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
                .name("Test Group Chat")
                .memberIds(memberIds)
                .type(ChatRoomType.GROUP)
                .build();

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "user1@test.com")); // Spring Security Principal 설정 (WithMockUser와 함께 사용)

        // Then
        resultActions
                .andExpect(status().isCreated()) // 201 Created 확인
                .andExpect(jsonPath("$.name").value("Test Group Chat"))
                .andExpect(jsonPath("$.type").value(ChatRoomType.GROUP.toString()))
                .andExpect(jsonPath("$.members.length()").value(3)) // 생성자 포함 3명
                .andExpect(jsonPath("$.members[?(@.userId == " + testUser1.getUserId() + ")].role").value("OWNER"))
                .andExpect(jsonPath("$.members[?(@.userId == " + testUser2.getUserId() + ")].role").value("MEMBER"))
                .andExpect(jsonPath("$.members[?(@.userId == " + testUser3.getUserId() + ")].role").value("MEMBER"));

        // 추가적으로 DB 상태 검증 로직을 넣을 수 있습니다.
    }

    @Test
    @DisplayName("1:1 채팅방 생성 성공")
    @WithMockUser(username = "user1@test.com")
    void testCreateDirectChatRoom_Success() throws Exception {
        // Given
        List<Long> memberIds = List.of(testUser2.getUserId()); // 1:1 채팅은 상대방 ID만
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
                .name("Direct Chat with user2") // 1:1 채팅은 이름이 중요하지 않을 수 있음
                .memberIds(memberIds)
                .type(ChatRoomType.DIRECT)
                .build();

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "user1@test.com"));

        // Then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChatRoomType.DIRECT.toString()))
                .andExpect(jsonPath("$.members.length()").value(2)) // 1:1은 2명
                .andExpect(jsonPath("$.members[?(@.userId == " + testUser1.getUserId() + ")].role").value("OWNER"))
                .andExpect(jsonPath("$.members[?(@.userId == " + testUser2.getUserId() + ")].role").value("MEMBER"));

        // 추가적으로 DB 상태 검증 로직 (예: 중복 생성되지 않는지 등)
    }

    // 필요한 경우 실패 케이스 (잘못된 요청 등) 테스트 추가

}
