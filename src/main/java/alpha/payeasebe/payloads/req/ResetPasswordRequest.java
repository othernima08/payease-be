package alpha.payeasebe.payloads.req;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ResetPasswordRequest {
   
    @NotEmpty(message = "Password is required!")
    private String newPassword;
    @NotEmpty(message = "Password is required!")
    private String confirmPassword;
}
