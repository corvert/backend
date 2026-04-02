package investTracker.controllers;

import investTracker.dtos.cash.CashTransactionResponse;
import investTracker.dtos.cash.CreateCashTransactionRequest;
import investTracker.services.CashTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-transactions")
public class CashTransactionController {
    @Autowired
    private CashTransactionService cashTransactionService;

    @PostMapping
    public CashTransactionResponse create(@Valid @RequestBody CreateCashTransactionRequest request){
        return cashTransactionService.create(request);
    }

    @GetMapping
    public List<CashTransactionResponse> transactionList(@RequestParam Long accountId){
        return cashTransactionService.transactionList(accountId);
    }
}
