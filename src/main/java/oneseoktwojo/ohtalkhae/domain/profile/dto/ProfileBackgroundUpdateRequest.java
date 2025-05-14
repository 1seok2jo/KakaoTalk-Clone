package oneseoktwojo.ohtalkhae.domain.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileBackgroundUpdateRequest {

    @Size(max = 200)
    private String backgroundDescription;

    private MultipartFile backgroundImage;
}
