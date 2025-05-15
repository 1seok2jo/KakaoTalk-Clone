package oneseoktwojo.ohtalkhae.domain.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusMessageUpdateRequest {

    @Size(max = 30, message = "상태 메시지는 최대 30자까지 입력 가능합니다.")
    private String statusMessage;
}
