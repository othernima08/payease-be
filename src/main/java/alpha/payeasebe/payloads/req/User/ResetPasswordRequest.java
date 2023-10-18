package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotEmpty(message = "Password is required!")
    @Size(min = 8, message = "Minimum password length is 8")
    private String newPassword;

    @NotEmpty(message = "Password is required!")
    private String confirmPassword;
}
