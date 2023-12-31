package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.payloads.req.Transactions.TransferRequest;
import alpha.payeasebe.services.transactions.TransactionsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    @Autowired 
    TransactionsService transactionsService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody @Valid TopUpRequest request) {
        return transactionsService.topUpGenerateCodeService(request);
    }

    @PutMapping("/top-up/{paymentCode}")
    public ResponseEntity<?> topUpPay(@PathVariable String paymentCode) {
        return transactionsService.topUpPaymentCodeService(paymentCode);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid TransferRequest request) {
        return transactionsService.transferService(request);
    }

    @GetMapping("/transfer/{id}")
    public ResponseEntity<?> transferDetailById(@PathVariable String id) {
        return transactionsService.transferDetail(id);
    }
   

    @GetMapping("/top-up-history/{userId}")
    public ResponseEntity<?> getTopUpHistoryByUserId(@PathVariable String userId) {
        return transactionsService.getTopUpHistoryByUserIdService(userId);
    }

    @GetMapping("/top-up-history")
    public ResponseEntity<?> getTopUpHistoryByUserIdAndStatus(@RequestParam String userId, @RequestParam Boolean isSuccess) {
        return transactionsService.getTopUpHistoryByUserIdAndStatusService(userId, isSuccess);
    }

    @GetMapping("/transaction-history/{userId}")
    public ResponseEntity<?> getTransactionHistoryByUserId(@PathVariable String userId) {
        return transactionsService.getTransactionHistoryByUserIdService(userId);
    }

    @GetMapping("/transaction-history")
    public ResponseEntity<?> getTransactionHistoryByUserIdAndType(@RequestParam String userId, @RequestParam Boolean isIncome) {
        return transactionsService.getTransactionHistoryByUserIdAndTypeService(userId, isIncome);
    }

    @GetMapping("/top-five-transaction-history/{userId}")
    public ResponseEntity<?> getTopFiveUserTransactionHistory(@PathVariable String userId) {
        return transactionsService.getTopFiveTransactionByUserId(userId);
    }

    @GetMapping("/transaction-history-filter-date")
    public ResponseEntity<?> getTransactionHistoryByUserIdAndDate(@RequestParam String userId, @RequestParam String startDate, @RequestParam String endDate) {
        return transactionsService.getTransactionHistoryByUserIdAndDateTimeService(userId, startDate, endDate);
    }

    @GetMapping("/amount-income-expense-user/{userId}")
    public ResponseEntity<?> getUserIncomesExpensesAmount(@PathVariable String userId) {
        return transactionsService.getIncomesAndExpensesAmountByUserId(userId);
    }

    @GetMapping("/transaction-history-daily/{userId}")
    public ResponseEntity<?> getTransactionHistoryByUserIdAndDays(@PathVariable String userId) {
        return transactionsService.getTransactionHistoryByUserIdAndDays(userId);
    }
}
