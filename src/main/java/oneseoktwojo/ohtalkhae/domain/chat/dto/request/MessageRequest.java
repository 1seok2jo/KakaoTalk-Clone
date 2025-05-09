package oneseoktwojo.ohtalkhae.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    @NotNull(message = "채팅방 ID는 필수입니다")
    private Long roomId;
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    @Size(max = 5000, message = "메시지 내용은 5000자를 초과할 수 없습니다")
    private String content;
    
    private Long replyToMessageId;
    
    @Builder.Default
    private List<Long> mentionedUserIds = new ArrayList<>();
} 