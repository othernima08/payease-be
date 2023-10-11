package alpha.payeasebe.payloads.req.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePINRequest {
    @NotEmpty(message = "User email is required")
    private String emailUser;

    @NotEmpty(message = "PIN is required")
    @Size(min = 6, max = 6, message = "PIN length must be 6")
    private String pin;
}
