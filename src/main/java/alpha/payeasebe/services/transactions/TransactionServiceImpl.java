package alpha.payeasebe.services.transactions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alpha.payeasebe.models.TopUp;
import alpha.payeasebe.models.TransactionCategories;
import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.models.Transfers;
import alpha.payeasebe.models.User;
import alpha.payeasebe.models.UserVirtualAccount;
import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.payloads.req.Transactions.TransferRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.payloads.res.ResponseShowTopUpHistory;
import alpha.payeasebe.payloads.res.ResponseShowTransactionHistory;
import alpha.payeasebe.repositories.TopUpRepository;
import alpha.payeasebe.repositories.TransactionCategoryRepository;
import alpha.payeasebe.repositories.TransactionsRepository;
import alpha.payeasebe.repositories.TransferRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.repositories.UserVirtualAccountRepository;
import alpha.payeasebe.validators.TopUpValidation;
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
    UserVirtualAccountRepository userVirtualAccountRepository;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    TopUpRepository topUpRepository;

    @Autowired
    TransactionCategoriesValidation transactionCategoriesValidation;

    @Autowired
    TopUpValidation topUpValidation;

    @Override
    public Transactions createTransactionService(String userId, String transactionCategoryName, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        TransactionCategories transactionCategories = transactionCategoryRepository.findByType(transactionCategoryName);
        transactionCategoriesValidation.validateTransactionCategory(transactionCategories);

        Transactions transactions = new Transactions(user, transactionCategories, amount);
        transactionsRepository.save(transactions);

        return transactions;
    }

    @Override
    public ResponseEntity<?> transferService(TransferRequest request) {
        Transactions transactions = createTransactionService(request.getUserId(), "Transfer", request.getAmount());

        User user = transactions.getUser();

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        User recipient = userRepository.findByPhoneNumber(request.getRecipientPhoneNumber());
        userValidation.validateUser(recipient);

        if (recipient.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        if (user.getBalance() < request.getAmount()) {
            transactions.setIsDeleted(true);
            transactionsRepository.save(transactions);

            throw new IllegalArgumentException("Insufficient balance");
        }

        Transfers transfers = new Transfers(recipient, transactions, request.getNotes(),request.getTransactionTime());
        transferRepository.save(transfers);

        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            transactions.setIsDeleted(true);
            transactionsRepository.save(transactions);

            throw new NoSuchElementException("Bad credentials: pin doesn't match!");
        }

        transactions.setAlreadyDone(true);
        transactionsRepository.save(transactions);

        user.setBalance(user.getBalance() - request.getAmount());
        userRepository.save(user);

        recipient.setBalance(recipient.getBalance() + request.getAmount());
        userRepository.save(recipient);

        return ResponseHandler.responseData(200,
                "Transfer from " + user.getFirstName() + " " + user.getLastName() + " success", transfers.getId() );
    }

    @Override
    public ResponseEntity<?> getTopUpHistoryByUserIdService(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTopUpHistory> topUpHistory = transactionsRepository.getTopUpHistoryByUserId(userId);

        return ResponseHandler.responseData(200, "Get top up history data success", topUpHistory);
    }

    @Override
    public ResponseEntity<?> transferDetail(String id) {
        Transfers transfers = transferRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("transfer not found");
        });

          return ResponseHandler.responseData(200, "Get top up history data success", transfers);
    }

    public ResponseEntity<?> topUpGenerateCodeService(TopUpRequest request) {
        Transactions transaction = createTransactionService(request.getUserId(), "Top Up", request.getAmount());

        String paymentCode = generatePaymentCode();

        UserVirtualAccount userVirtualAccount = userVirtualAccountRepository.findById(request.getVirtualAccountId())
                .orElseThrow(() -> new NoSuchElementException("Virtual account is not found"));

        TopUp topUp = new TopUp(transaction, userVirtualAccount, paymentCode);
        topUpRepository.save(topUp);

        Map<String, Object> data = new HashMap<>();
        data.put("virtualAccountId", topUp.getUserVirtualAccount().getId());
        data.put("paymentCode", topUp.getPaymentCode());

        return ResponseHandler.responseData(200, "Success", data);
    }

    @Override
    public ResponseEntity<?> topUpPaymentCodeService(String paymentCode) {
        TopUp topUp = topUpRepository.findByPaymentCode(paymentCode);

        topUpValidation.validateTopUp(topUp);

        if (topUp.getIsExpired()) {
            throw new IllegalArgumentException("Payment code already expired");
        }

        Transactions transaction = transactionsRepository.findById(topUp.getTransactions().getId())
                .orElseThrow(() -> new NoSuchElementException("Transaction is not found"));

        User user = transaction.getUser();

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        transaction.setAlreadyDone(true);
        transactionsRepository.save(transaction);

        user.setBalance(user.getBalance() + transaction.getAmount());
        userRepository.save(user);

        topUp.setIsExpired(true);
        topUpRepository.save(topUp);

        return ResponseHandler.responseMessage(200,
                "Top up for " + user.getFirstName() + " " + user.getLastName() + " success", true);
    }

    public String generatePaymentCode() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        Integer CODE_LENGTH = 12;

        StringBuilder code = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHA_NUMERIC_STRING.length());
            char randomChar = ALPHA_NUMERIC_STRING.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }

    @Override
    public ResponseEntity<?> getTopUpHistoryByUserIdAndStatusService(String userId, Boolean isDeleted) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTopUpHistory> topUpHistory = transactionsRepository.getTopUpHistoryByUserIdAndStatus(userId,
                isDeleted);

        return ResponseHandler.responseData(200, "Get top up history data success", topUpHistory);
    }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdService(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        Collections.sort(transactionHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " transaction history success",
                transactionHistories);
    }

    // @Override
    // public ResponseEntity<?> getIncomeTransactionHistoryByUserIdService(String userId) {
    //     User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

    //     if (user.getIsDeleted()) {
    //         throw new IllegalArgumentException("User is not active or already deleted");
    //     }

    //     List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
    //     transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
    //     transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));

    //     Collections.sort(transactionHistories,
    //             (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

    //     return ResponseHandler.responseData(200,
    //             "Get " + user.getFirstName() + " " + user.getLastName() + " income transaction history success",
    //             transactionHistories);
    // }

    // @Override
    // public ResponseEntity<?> getExpenseTransactionHistoryByUserIdService(String userId) {
    //     User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

    //     if (user.getIsDeleted()) {
    //         throw new IllegalArgumentException("User is not active or already deleted");
    //     }

    //     List<ResponseShowTransactionHistory> transactionHistories = transactionsRepository
    //             .getTransferToHistoryByUserId(userId);

    //     return ResponseHandler.responseData(200,
    //             "Get " + user.getFirstName() + " " + user.getLastName() + " transaction history success",
    //             transactionHistories);
    // }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdAndTypeService(String userId, Boolean isIncome) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();

        if (isIncome) {
            transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
            transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));

            Collections.sort(transactionHistories,
                    (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));
        } else {
            transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));
        }

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " transaction history success",
                transactionHistories);
    }
}
