package alpha.payeasebe.payloads.req.TransactionCategories;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateTransactionCategoriesRequest {
    @NotEmpty(message = "Transaction category type is required")
    private String type;
}
