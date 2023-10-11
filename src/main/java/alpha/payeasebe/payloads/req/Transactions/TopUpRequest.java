package alpha.payeasebe.payloads.req.Transactions;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TopUpRequest {
    @NotEmpty(message = "User id is required")
    private String userId;

    private Double amount;
}
