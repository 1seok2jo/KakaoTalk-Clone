package oneseoktwojo.ohtalkhae.domain.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 친구요청 수락/거절 DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {
    @NotNull
    private Long requestId;
}
