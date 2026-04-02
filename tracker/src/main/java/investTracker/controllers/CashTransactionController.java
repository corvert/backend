package investTracker.controllers;

import investTracker.dtos.cash.CashTransactionResponse;
import investTracker.dtos.cash.CreateCashTransactionRequest;
import investTracker.services.CashTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cash-transactions")
public class CashTransactionController {
    @Autowired
    private CashTransactionService cashTransactionService;

    @PostMapping
    public CashTransactionResponse create(@Valid @RequestBody CreateCashTransactionRequest request){
        return cashTransactionService.create(request);
    }
}
