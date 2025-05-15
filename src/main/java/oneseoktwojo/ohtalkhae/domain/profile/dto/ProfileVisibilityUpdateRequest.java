package oneseoktwojo.ohtalkhae.domain.profile.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileVisibilityUpdateRequest {
    private boolean isPublic; // 프로필 공개 여부
}
