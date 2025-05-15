package oneseoktwojo.ohtalkhae.domain.friend.dto;

import lombok.Data;

// 친구 목록 조회 및 요청 목록 조회 응답용 DTO
@Data
public class FriendListDto {
    private Long friendId;
    private String name;
    private String nickname;
    private String phoneNumber;

}
