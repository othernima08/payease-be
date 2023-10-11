package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePhoneNumberRequest {

    @NotEmpty(message = "User email is required")
    private String userId;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    @Size(min = 12, max = 13, message = "Phone number length must be 12 or 13")
    private String phoneNumber;
}
