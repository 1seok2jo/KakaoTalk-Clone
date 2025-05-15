package oneseoktwojo.ohtalkhae.domain.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

//친구 요청 API 요청용 DTO

@Data
public class FriendRequestDto {
    @NotNull
    private Long fromId;  // 요청 보내는 회원 ID
    @NotNull
    private Long toId;    // 요청 받는 회원 ID
}
