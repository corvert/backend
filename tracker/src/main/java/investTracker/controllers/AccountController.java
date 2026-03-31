package investTracker.controllers;

import investTracker.dtos.AccountResponse;
import investTracker.dtos.CreateAccountRequest;
import investTracker.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request){
        return accountService.create(request);
    }

    @GetMapping
    public List<AccountResponse> accountList(){
        return accountService.accountList();
    }

    @PutMapping("/{accountId}")
    public AccountResponse update(@PathVariable Long accountId, @RequestBody CreateAccountRequest request){
        return accountService.update(accountId, request);

    }

}
