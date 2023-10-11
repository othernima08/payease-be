package alpha.payeasebe.services.transactionCategories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import alpha.payeasebe.exceptions.custom.EntityFoundException;
import alpha.payeasebe.models.TransactionCategories;
import alpha.payeasebe.payloads.req.TransactionCategories.CreateTransactionCategoriesRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.TransactionCategoryRepository;

@Service
public class TransactionCategoriesServiceImpl implements TransactionCategoriesService {
    @Autowired
    TransactionCategoryRepository transactionCategoryRepository;

    @Override
    public ResponseEntity<?> createTransactionCategoryService(CreateTransactionCategoriesRequest request) {
        if (transactionCategoryRepository.existsByType(request.getType())) {
            throw new EntityFoundException("Transaction category already exists");
        }

        TransactionCategories transactionCategories = new TransactionCategories(request.getType());
        transactionCategoryRepository.save(transactionCategories);

        return ResponseHandler.responseMessage(200, "Transaction category created successfully", true);
    }
}
