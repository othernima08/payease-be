package alpha.payeasebe.services.transactions;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.payloads.req.Transactions.TransferRequest;

public interface TransactionsService {
    Transactions createTransactionService(String userId, String transactionCategoryName, Double amount);
    ResponseEntity<?> topUpGenerateCodeService(TopUpRequest request);
    ResponseEntity<?> topUpPaymentCodeService(String paymentCode);
    ResponseEntity<?> transferService(TransferRequest request);
    ResponseEntity<?> getTopUpHistoryByUserIdService(String userId);
    ResponseEntity<?> getTopUpHistoryByUserIdAndStatusService(String userId, Boolean isDeleted);
    ResponseEntity<?> getTransactionHistoryByUserIdService(String userId);
    // ResponseEntity<?> getIncomeTransactionHistoryByUserIdService(String userId);
    // ResponseEntity<?> getExpenseTransactionHistoryByUserIdService(String userId);
    ResponseEntity<?> getTransactionHistoryByUserIdAndTypeService(String userId, Boolean isIncome);
}
