package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotEmpty(message = "User email is required")
    private String emailUser;
    
    @NotEmpty(message = "Password is required!")
    private String newPassword;
}
