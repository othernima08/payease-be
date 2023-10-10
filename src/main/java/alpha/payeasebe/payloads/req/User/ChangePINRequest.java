package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePINRequest {
    @NotEmpty(message = "User id is required")
    private String userId;

    @NotEmpty(message = "Current PIN is required")
    @Size(min = 6, max = 6, message = "PIN length must be 6")
    private String currentPin;

    @NotEmpty(message = "New PIN is required")
    @Size(min = 6, max = 6, message = "PIN length must be 6")
    private String newPin;
}
