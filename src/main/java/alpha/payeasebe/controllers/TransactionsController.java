package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/top-up-history/{userId}")
    public ResponseEntity<?> getTopUpHistoryByUserId(@PathVariable String userId) {
        return transactionsService.getTopUpHistoryByUserIdService(userId);
    }
}
