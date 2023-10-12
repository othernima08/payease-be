package alpha.payeasebe.payloads.req.Providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateProvidersRequest {
    @NotEmpty(message = "Provider name is required")
    private String name;

    private String profilePicture;
}
