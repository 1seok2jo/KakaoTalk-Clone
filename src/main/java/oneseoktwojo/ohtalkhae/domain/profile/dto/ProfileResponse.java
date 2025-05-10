package oneseoktwojo.ohtalkhae.domain.profile.dto;

import lombok.Getter;
import lombok.Setter;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;

@Setter
@Getter
public class ProfileResponse {

    private String nickname;
    private String statusMessage;
    private String profileImagePath;

    public ProfileResponse(User user) {
        this.nickname = user.getNickname();
        this.statusMessage = user.getStatusMessage();
        this.profileImagePath = user.getProfileImagePath();
    }
}
