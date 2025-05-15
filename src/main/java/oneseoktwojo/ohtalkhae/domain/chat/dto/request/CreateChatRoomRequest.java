package oneseoktwojo.ohtalkhae.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.ChatRoomType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomRequest {

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    @Size(max = 50, message = "채팅방 이름은 최대 50자까지 가능합니다.")
    private String name;

    @NotEmpty(message = "채팅방 멤버는 필수입니다.")
    private List<Long> memberIds;

    @NotNull(message = "채팅방 타입은 필수입니다.")
    private ChatRoomType type;
} 