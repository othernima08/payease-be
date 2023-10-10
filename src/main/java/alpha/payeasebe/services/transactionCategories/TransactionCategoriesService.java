package alpha.payeasebe.services.transactionCategories;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.TransactionCategories.CreateTransactionCategoriesRequest;

public interface TransactionCategoriesService {
    ResponseEntity<?> createTransactionCategoryService(CreateTransactionCategoriesRequest request);
}
