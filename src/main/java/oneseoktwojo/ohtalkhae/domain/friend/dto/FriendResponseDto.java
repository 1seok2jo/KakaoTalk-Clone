package oneseoktwojo.ohtalkhae.domain.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 친구요청 수락/거절 DTO

@Data
public class FriendResponseDto {
    @NotNull
    private Long requestId;
}
