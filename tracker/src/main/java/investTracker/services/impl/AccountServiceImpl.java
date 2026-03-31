package investTracker.services.impl;

import investTracker.dtos.AccountResponse;
import investTracker.dtos.CreateAccountRequest;
import investTracker.models.Account;
import investTracker.repositories.AccountRepository;
import investTracker.services.AccountService;
import investTracker.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthUtil authUtil;

    public AccountResponse create(CreateAccountRequest request){
        Long userId = authUtil.loggedInUserId();
        if (accountRepository.existsByUserIdAndAccountNameIgnoreCase(userId, request.getName().trim())) {
            throw new IllegalArgumentException("Account with the same name already exists");
        }
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountName(request.getName().trim());
        account.setAccountKind(request.getAccountKind());
        account.setTradingMode(request.getTradingMode());
        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

    public List<AccountResponse> accountList(){
        Long userId = authUtil.loggedInUserId();
        return accountRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountResponse update(Long accountId, CreateAccountRequest request) {
        Long userId = authUtil.loggedInUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }

        if (!account.getAccountName().equalsIgnoreCase(request.getName().trim()) &&
                accountRepository.existsByUserIdAndAccountNameIgnoreCase(userId, request.getName().trim())) {
            throw new IllegalArgumentException("Account with the same name already exists");
        }

        account.setAccountName(request.getName().trim());
        account.setAccountKind(request.getAccountKind());
        account.setTradingMode(request.getTradingMode());
        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }




    private AccountResponse toResponse(Account account){
        return new AccountResponse(
                account.getId(),
                account.getAccountName(),
                account.getAccountKind(),
                account.getTradingMode(),
                account.getCreatedAt()
        );
    }
}
