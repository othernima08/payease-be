package alpha.payeasebe.payloads.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegisterRequest {
    @NotEmpty(message = "First name is required!")   
    private String firstName;
    
    private String lastName;

    @Email(message = "Must be email format!", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email is required!")
    private String email;

    @NotEmpty(message = "Password is required!")
    private String password;
}
