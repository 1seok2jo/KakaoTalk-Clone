package oneseoktwojo.ohtalkhae.domain.emoji;

import com.fasterxml.jackson.databind.ObjectMapper;
import oneseoktwojo.ohtalkhae.domain.emoji.controller.EmojiController;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.request.EmojiRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiDetailResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiListResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiPurchaseCheckResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiRegisterResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.service.EmojiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmojiController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmojiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmojiService emojiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("이모티콘 등록 성공")
    void registerEmoji_success() throws Exception {
        EmojiRegisterRequest request = EmojiRegisterRequest.builder()
                .emojiName("테스트 이모티콘")
                .emojiPrice(3000)
                .mainEmojiUrl("https://example.com/main.png")
                .emojiUrls(List.of("https://example.com/1.png"))
                .sellerName("테스트 판매자")
                .build();

        EmojiRegisterResponse response = EmojiRegisterResponse.builder()
                .emojiId(1L)
                .emojiName("테스트 이모티콘")
                .detailPageUrl("/emojis/1")
                .build();

        Mockito.when(emojiService.registerEmoji(any(EmojiRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/emojis/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emojiId").value(1L))
                .andExpect(jsonPath("$.data.emojiName").value("테스트 이모티콘"));
    }

    @Test
    @DisplayName("이모티콘 전체 조회")
    void getAllEmojis() throws Exception {
        EmojiListResponse emoji = EmojiListResponse.builder()
                .emojiId(1L)
                .emojiName("테스트 이모티콘")
                .mainEmojiUrl("url")
                .emojiPrice(1000)
                .build();

        Mockito.when(emojiService.getAllEmojis(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(emoji)));

        mockMvc.perform(get("/emojis?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].emojiName").value("테스트 이모티콘"));
    }

    @Test
    @DisplayName("이모티콘 상세 조회")
    void getEmojiDetail() throws Exception {
        EmojiDetailResponse detail = EmojiDetailResponse.builder()
                .emojiId(1L)
                .emojiName("테스트 이모티콘")
                .emojiUrls(List.of("url1", "url2"))
                .build();

        Mockito.when(emojiService.getEmojiDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/emojis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emojiId").value(1L))
                .andExpect(jsonPath("$.data.emojiUrls[0]").value("url1"));
    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("이모티콘 북마크 추가")
    void addBookmark() throws Exception {
        Mockito.doNothing().when(emojiService).addBookmark("user1", 1L);

        mockMvc.perform(post("/emojis/1/bookmark")
                        .param("userId", "user1"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("이모티콘 북마크 삭제")
    void removeBookmark() throws Exception {
        Mockito.doNothing().when(emojiService).removeBookmark("user1", 1L);

        mockMvc.perform(delete("/emojis/1/bookmark")
                        .param("userId", "user1"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("이모티콘 구매")
    void buyEmoji() throws Exception {
        Mockito.doNothing().when(emojiService).buyEmoji("user1", 1L);

        mockMvc.perform(post("/emojis/1/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emojiId\":1}"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user1")
    @Test
    @DisplayName("이모티콘 구매 여부 확인")
    void checkPurchase() throws Exception {
        EmojiPurchaseCheckResponse resp = new EmojiPurchaseCheckResponse(true);

        Mockito.when(emojiService.checkPurchase("user1", 1L)).thenReturn(resp);

        mockMvc.perform(get("/emojis/1/purchase/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.purchased").value(true));
    }
}