package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "User id is required")
    private String userId;

    @NotEmpty(message = "Current Password is required")
    private String currentPassword;

    @NotEmpty(message = "New Password is required")
    @Size(min = 8, message = "Minimum password length is 8")
    private String newPassword;
}
