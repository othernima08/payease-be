package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.services.transactions.TransactionsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    @Autowired 
    TransactionsService transactionsService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody @Valid TopUpRequest request) {
        return transactionsService.topUpService(request);
    }
}
