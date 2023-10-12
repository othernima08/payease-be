package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.TransactionCategories.CreateTransactionCategoriesRequest;
import alpha.payeasebe.services.transactionCategories.TransactionCategoriesService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transaction-categories")
public class TransactionCategoriesController {
    @Autowired
    TransactionCategoriesService transactionCategoriesService;

    @PostMapping()
    public ResponseEntity<?> createTransactionHistory(@RequestBody @Valid CreateTransactionCategoriesRequest request) {
        return transactionCategoriesService.createTransactionCategoryService(request);
    }
}
