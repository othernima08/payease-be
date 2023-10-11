package alpha.payeasebe.services.transactions;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alpha.payeasebe.models.TransactionCategories;
import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.TransactionCategoryRepository;
import alpha.payeasebe.repositories.TransactionsRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.validators.TransactionCategoriesValidation;
import alpha.payeasebe.validators.UserValidation;

@Service
public class TransactionServiceImpl implements TransactionsService {
    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;
    
    @Autowired 
    PasswordEncoder passwordEncoder;

    @Autowired
    TransactionCategoriesValidation transactionCategoriesValidation;

    @Override
    public Transactions createTransactionService(String userId, String transactionCategoryName, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));
        
        TransactionCategories transactionCategories = transactionCategoryRepository.findByType(transactionCategoryName);
        transactionCategoriesValidation.validateTransactionCategory(transactionCategories);

        Transactions transactions = new Transactions(user, transactionCategories, amount);
        transactionsRepository.save(transactions);

        return transactions;
    }

    @Override
    public ResponseEntity<?> getTransactionByUserId(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionByUserId'");
    }

    @Override
    public ResponseEntity<?> topUpService(TopUpRequest request) {
        Transactions transactions = createTransactionService(request.getUserId(), "Top Up", request.getAmount());

        User user = transactions.getUser();
        System.out.println(user);

        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new NoSuchElementException("Bad credentials: pin doesn't match!");
        }

        transactions.setAlreadyDone(true);
        transactionsRepository.save(transactions);

        user.setBalance(user.getBalance() + transactions.getAmount());
        userRepository.save(user);

        return ResponseHandler.responseMessage(200, "Top up for " + user.getFirstName() + " " + user.getLastName() + " success", true);
    }    
}
