package investTracker.controllers;

import investTracker.dtos.cash.CashBalanceResponse;
import investTracker.services.CashTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class CashBalanceController {
    @Autowired
    private CashTransactionService cashTransactionService;

    @GetMapping("/{accountId}/cash-balance")
    public List<CashBalanceResponse> getBalances(@PathVariable Long accountId){
        return cashTransactionService.calculateBalances(accountId);
    }

}
