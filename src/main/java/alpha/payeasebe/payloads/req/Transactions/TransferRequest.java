package alpha.payeasebe.payloads.req.Transactions;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TransferRequest {
    @NotEmpty(message = "User id is required")
    private String userId;
    
    @NotEmpty(message = "Recipient phone number is required")
    private String recipientPhoneNumber;

    @NotEmpty(message = "PIN is required")
    private String pin;

    private Double amount;

    private LocalDateTime transactionTime;


    private String notes;
}
