package oneseoktwojo.ohtalkhae.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 친구 목록 조회 및 요청 목록 조회 응답용 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListDto {
    private Long friendId;
    private String name;
    private String nickname;
    private String phoneNumber;

}
