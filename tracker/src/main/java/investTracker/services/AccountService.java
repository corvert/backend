package investTracker.services;

import investTracker.dtos.account.AccountResponse;
import investTracker.dtos.account.CreateAccountRequest;

import java.util.List;

public interface AccountService {
    AccountResponse create(CreateAccountRequest request);

    List<AccountResponse> accountList();

    AccountResponse update(Long accountId, CreateAccountRequest request);
}
