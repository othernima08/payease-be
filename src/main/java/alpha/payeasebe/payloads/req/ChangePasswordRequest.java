package alpha.payeasebe.payloads.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "User id is required")
    private String userId;

    @NotEmpty(message = "Current Password is required")
    private String currentPassword;

    @NotEmpty(message = "New Password is required")
    private String newPassword;
}
