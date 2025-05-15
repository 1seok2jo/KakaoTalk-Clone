package oneseoktwojo.ohtalkhae.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.chat.enums.Role;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberDto {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private Role role;
    private boolean notificationEnabled;
    private Long lastReadMessageId;
} 