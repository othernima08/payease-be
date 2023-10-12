package alpha.payeasebe.validators;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import alpha.payeasebe.models.TransactionCategories;

@Service
public class TransactionCategoriesValidation {
    public void validateTransactionCategory(TransactionCategories transactionCategories) {
        if(transactionCategories == null || Objects.isNull(transactionCategories)){
            throw new NoSuchElementException("Transaction category is not found");
        }
    }
}
