package alpha.payeasebe.payloads.req.OTP;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VerifyOTPRequest {
    @Email(message = "Must be email format!", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email is required!")
    private String emailUser;

    @NotEmpty(message = "OTP is required!")
    private String otpCode;
}
